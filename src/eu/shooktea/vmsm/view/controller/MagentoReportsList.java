package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.module.MagentoReport;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class MagentoReportsList {

    @FXML private TableView<MagentoReport> exceptionTable;
    @FXML private Label timeLabel;

    @FXML
    private void initialize() {

    }

    public static void openMagentoReportsList(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/MagentoReportsList.fxml", "Exception reports", false);
    }
}
