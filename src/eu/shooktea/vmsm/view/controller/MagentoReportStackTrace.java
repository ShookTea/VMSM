package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.module.MagentoReport;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MagentoReportStackTrace {

    @FXML private TextArea stackTrace;

    private void setReport(MagentoReport report) {
        stackTrace.setText(report.getText());
    }


    public static void openStackTraceWindow(MagentoReport report) {
        if (report == null) return;
        MagentoReportStackTrace mrst =
                Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/MagentoReportStackTrace.fxml", "Stack trace \"" + report.getText() + "\"", false);
        mrst.setReport(report);
    }
}
