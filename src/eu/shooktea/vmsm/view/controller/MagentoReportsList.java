package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MagentoReport;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MagentoReportsList {

    @FXML private TableView<MagentoReport> exceptionTable;
    @FXML private Label timeLabel;

    private Magento magento;
    private VirtualMachine vm;

    @FXML
    private void initialize() {
        vm = Start.virtualMachineProperty.get();
        magento = (Magento)Magento.getModuleByName("Magento");
        MagentoReport.HoldTime time = MagentoReport.HoldTime.fromTime(MagentoReport.MAX_TIME_DIFFERENCE);
        if (time == MagentoReport.HoldTime.NEVER) timeLabel.setText("");
        else if (time == MagentoReport.HoldTime.ETERNITY) timeLabel.setText("From eternity");
        else timeLabel.setText("From the last " + time.toString());

        ObservableList<TableColumn<MagentoReport, ?>> columns = exceptionTable.getColumns();
        columns.clear();

        TableColumn<MagentoReport, String> date = new TableColumn<>("Time");
        date.setCellValueFactory(new PropertyValueFactory<>("stringDate"));

        columns.add(date);

        exceptionTable.setItems(MagentoReport.allReports);
    }

    public static void openMagentoReportsList(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/MagentoReportsList.fxml", "Exception reports", true);
    }
}