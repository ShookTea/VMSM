package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.module.mage.Report;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MagentoReportStackTrace {

    @FXML private TextArea stackTrace;

    private void setReport(Report report) {
        stackTrace.setText(report.getText());
    }


    public static void openStackTraceWindow(Report report) {
        if (report == null) return;
        MagentoReportStackTrace mrst =
                View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/MagentoReportStackTrace.fxml", "Stack trace \"" + report.getText() + "\"");
        mrst.setReport(report);
    }
}
