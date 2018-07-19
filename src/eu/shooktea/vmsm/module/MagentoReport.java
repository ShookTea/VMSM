package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MagentoReport {
    private MagentoReport(String name, long timestamp, String text) {
        this.name = name;
        this.timestamp = timestamp;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public final String name;
    public final long timestamp;
    public final String text;

    public static void update(Magento module, VirtualMachine vm, File reportsDir) {
        if (MAX_TIME_DIFFERENCE == -1) {
            Long value = (Long)module.getSetting(vm, "report_keep_time");
            MAX_TIME_DIFFERENCE = value == null ? MAX_TIME_DIFFERENCE_DEFAULT : value;
        }
        CHANGES = false;
        List<MagentoReport> reports = getReportsFromConfig(module, vm, reportsDir);
        List<String> reportNames = reports.stream()
                .map(MagentoReport::getName)
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
                    MagentoReport newReport = new MagentoReport(name, timestamp, text);
                    reports.add(newReport);
                    if (timestamp - file.lastModified() < 1000 * 60) notifyNewReport(newReport); //not notify if report is older than one minute
                    CHANGES = true;
                });
        storeReportsInConfig(module, vm, reports);
    }

    private static void notifyNewReport(MagentoReport report) {

    }

    private static void storeReportsInConfig(Magento module, VirtualMachine vm, List<MagentoReport> reports) {
        JSONArray array = new JSONArray();
        for (MagentoReport report : reports) {
            JSONObject obj = new JSONObject();
            obj.put("name", report.name);
            obj.put("timestamp", report.timestamp);
            obj.put("text", report.text);
            array.put(obj);
        }
        module.setSetting(vm, "reports", array);
        if (CHANGES) Storage.saveAll();
    }

    private static List<MagentoReport> getReportsFromConfig(Magento module, VirtualMachine vm, File reportsDir) {
        Object reportsObj = module.getSetting(vm, "reports");
        if (reportsObj == null) reportsObj = new JSONArray();
        if (!(reportsObj instanceof JSONArray)) throw new RuntimeException("Magento reports are not array; reports.toString = \"" + reportsObj.toString() + "\"");
        JSONArray reports = (JSONArray)reportsObj;
        List<MagentoReport> reportsFromConfig = new ArrayList<>();
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
            if (keep) reportsFromConfig.add(new MagentoReport(name, timestamp, text));
            if (!keep) CHANGES = true;
        });
        return reportsFromConfig;
    }

    private static long MAX_TIME_DIFFERENCE = -1;
    private static long MAX_TIME_DIFFERENCE_DEFAULT = 1000L * 60 * 60 * 24 * 30; //30 days
    private static boolean CHANGES = false;
}
