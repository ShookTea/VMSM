package eu.shooktea.vmsm.module.mage;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * Class representing single exception report in Magento.
 */
public class Report {
    private Report(String name, long timestamp, String text) {
        this.name = new SimpleStringProperty(name);
        this.timestamp = new SimpleLongProperty(timestamp);
        this.text = new SimpleStringProperty(text);
        this.time = new SimpleObjectProperty(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()));
    }

    /**
     * Returns name of report.
     * @return name
     */
    public String getName() {
        return name.getValue();
    }

    /**
     * Returns Unix timestamp when report has been made.
     * @return Unix timestamp in millis
     */
    public long getTimestamp() {
        return timestamp.getValue();
    }

    /**
     * Returns text containing stack trace of exception
     * @return exception text
     */
    public String getText() {
        return text.getValue();
    }

    /**
     * Returns parsed timestamp
     * @return local date time
     */
    public LocalDateTime getTime() {
        return time.getValue();
    }

    /**
     * Returns message of exception, loaded from its text.
     * @return message of exception
     */
    public String getMessage() {
        return getText().replaceFirst("^.:\\d+:\\{.:\\d+;.:\\d*:\"([^\"]*)\"[\\s\\S]*$", "$1");
    }

    private final ReadOnlyStringProperty name;
    private final ReadOnlyLongProperty timestamp;
    private final ReadOnlyStringProperty text;
    private final ReadOnlyObjectProperty<LocalDateTime> time;

    /**
     * Checks if there are any new reports.
     * @param module magento module
     * @param vm virtual machine
     * @param reportsDir directory with reports
     */
    public static void update(Magento module, VirtualMachine vm, File reportsDir) {
        if (previousMachine != vm) {
            previousMachine = vm;
            notifyReports.clear();
        }
        if (MAX_TIME_DIFFERENCE == -1) {
            Object v = module.getOldSettings(vm, "report_keep_time");
            Long value = null;
            if (v instanceof Long) {
                value = (Long)v;
            }
            else if (v instanceof Integer) {
                value = ((Integer)v).longValue();
            }
            //30 days
            long MAX_TIME_DIFFERENCE_DEFAULT = 1000L * 60 * 60 * 24 * 30;
            MAX_TIME_DIFFERENCE = value == null ? MAX_TIME_DIFFERENCE_DEFAULT : value;
        }
        CHANGES = false;
        allReports.clear();
        allReports.addAll(getReportsFromConfig(module, vm, reportsDir));
        List<String> reportNames = allReports.stream()
                .map(Report::getName)
                .collect(Collectors.toList());
        Arrays.stream(reportsDir.listFiles())
                .filter(file -> !reportNames.contains(file.getName()))
                .forEach(file -> {
                    String name = file.getName();
                    long timestamp = System.currentTimeMillis();
                    String text;
                    try {
                        text = new String(Files.readAllBytes(file.toPath())).trim();
                    } catch (IOException e) {
                        throw new RuntimeException("Cannot read report text from file " + file.toString(), e);
                    }
                    Report newReport = new Report(name, timestamp, text);
                    allReports.add(newReport);
                    notifyReports.add(newReport);
                    CHANGES = true;
                });
        storeReportsInConfig(module, vm, allReports);
    }

    /**
     * List of new reports that user has not yet seen.
     */
    public static ObservableList<Report> notifyReports = FXCollections.observableArrayList();
    /**
     * List of all reports stored by VMSM.
     */
    public static ObservableList<Report> allReports = FXCollections.observableArrayList();
    private static IntegerProperty newReportsCount = new SimpleIntegerProperty();
    private static VirtualMachine previousMachine = null;

    static {
        newReportsCount.bind(Bindings.createIntegerBinding(() -> notifyReports.size(), notifyReports));
    }

    private static void storeReportsInConfig(Magento module, VirtualMachine vm, List<Report> reports) {
        JSONArray array = new JSONArray();
        for (Report report : reports) {
            JSONObject obj = new JSONObject();
            obj.put("name", report.getName());
            obj.put("timestamp", report.getTimestamp());
            obj.put("text", report.getText());
            array.put(obj);
        }
        module.setOldSetting(vm, "reports", array);
        if (CHANGES) Storage.saveAll();
    }

    private static ObservableList<Report> getReportsFromConfig(Magento module, VirtualMachine vm, File reportsDir) {
        Object reportsObj = module.getOldSettings(vm, "reports");
        if (reportsObj == null) reportsObj = new JSONArray();
        if (!(reportsObj instanceof JSONArray)) throw new RuntimeException("Magento reports are not array; reports.toString = \"" + reportsObj.toString() + "\"");
        JSONArray reports = (JSONArray)reportsObj;
        ObservableList<Report> reportsFromConfig = FXCollections.observableArrayList();
        long currentTimestamp = System.currentTimeMillis();
        reports.iterator().forEachRemaining(obj -> {
            JSONObject json = (JSONObject)obj;
            String name = json.getString("name");
            long timestamp = json.getLong("timestamp");
            String text = json.getString("text");

            boolean keep = true;
            if (currentTimestamp - timestamp > MAX_TIME_DIFFERENCE) {
                keep = new File(reportsDir, name).exists();
            }
            if (keep) reportsFromConfig.add(new Report(name, timestamp, text));
            if (!keep) CHANGES = true;
        });
        return reportsFromConfig;
    }

    /**
     * Maximal difference in milliseconds between current time and time of report creation. If that difference
     * is surpassed, report will be removed from VMSM, unless report file still exists.
     * @see HoldTime
     */
    public static long MAX_TIME_DIFFERENCE = -1;
    private static boolean CHANGES = false;

    /**
     * Enum representing possible max time differences with label to display.
     * @see #MAX_TIME_DIFFERENCE
     */
    public enum HoldTime {
        NEVER("Never", 0),
        HOUR("1 hour", 1000L * 60 * 60),
        TWO_HOURS("2 hours", HOUR.timeMillis * 2),
        SIX_HOURS("6 hours", HOUR.timeMillis * 6),
        DAY("1 day", HOUR.timeMillis * 24),
        WEEK("1 week", DAY.timeMillis * 7),
        MONTH("30 days", DAY.timeMillis * 30),
        YEAR("1 year", DAY.timeMillis * 365),
        ETERNITY("Eternity", Long.MAX_VALUE);

        HoldTime(String name, long timeMillis) {
            this.name = name;
            this.timeMillis = timeMillis;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * Label representing given max time difference.
         */
        public final String name;
        /**
         * Max time difference.
         * @see #MAX_TIME_DIFFERENCE
         */
        public final long timeMillis;

        /**
         * Returns observable list of all {@link HoldTime} values.
         * @return list of {@code HoldTime} values.
         */
        public static ObservableList<HoldTime> createList() {
            return FXCollections.observableArrayList(HoldTime.values());
        }

        /**
         * Returns {@link HoldTime} based on its max time difference in milliseconds. If no correct {@code HoldTime}
         * is found, it will remove {@link HoldTime#NEVER}.
         * @param l max time difference in milliseconds
         * @return {@code HoldTime} with choosen max time difference; {@link HoldTime#NEVER} if no correct {@code HoldTime} is found.
         */
        public static HoldTime fromTime(long l) {
            return Arrays.stream(HoldTime.values())
                    .filter(t -> t.timeMillis == l)
                    .findAny().orElse(NEVER);
        }
    }
}