package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.VirtualMachine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        }

        reportsString = reportFiles.stream()
                .map(File::getName)
                .reduce("", (a, b) -> a + ";" + b);
        if (reportsString.startsWith(";")) reportsString = reportsString.substring(1);
        if (reportsString.endsWith(";")) reportsString = reportsString.substring(0, reportsString.length() - 1);
        module.setSetting(vm, "reports_list", reportsString);
    }
}
