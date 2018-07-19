package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.VirtualMachine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MagentoReport {
    private MagentoReport() {}

    public static void update(Magento module, VirtualMachine vm, File reportsDir) {
        String reportsString = module.getSetting(vm, "reports_list");
        if (reportsString == null) reportsString = "";
        List<String> parts = Arrays.asList(reportsString.split(";"));
        List<File> reportFiles = parts.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> new File(reportsDir, s))
                .filter(File::exists)
                .collect(Collectors.toCollection(ArrayList::new));

        List<File> newReports = new ArrayList<>();

        for (File reportFile : reportsDir.listFiles()) if (!reportFiles.contains(reportFile)) {
            reportFiles.add(reportFile);
            newReports.add(reportFile);
            module.setSetting(vm, "report_" + reportFile.getName() + "_time", Long.toString(System.currentTimeMillis()));
        }

        reportsString = reportFiles.stream()
                .map(File::getName)
                .map(num -> {
                    if (checkReportTime(module, vm, num)) return num;
                    else {
                        module.removeSetting(vm, "report_" + num + "_time");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .reduce("", (a, b) -> a + ";" + b);
        if (reportsString.startsWith(";")) reportsString = reportsString.substring(1);
        if (reportsString.endsWith(";")) reportsString = reportsString.substring(0, reportsString.length() - 1);
        module.setSetting(vm, "reports_list", reportsString);
    }

    private static boolean checkReportTime(Magento module, VirtualMachine vm, String reportNumber) {
        String time = module.getSetting(vm, "report_" + reportNumber + "_time");
        if (time == null) return false; //no time entry, to remove
        long timestamp = Long.parseLong(time);
        long currentTimestamp = System.currentTimeMillis();
        return currentTimestamp - timestamp < MAX_TIME_DIFFERENCE;
    }

    private static long MAX_TIME_DIFFERENCE = 1000L * 60 * 60 * 24 * 30; //30 days
}
