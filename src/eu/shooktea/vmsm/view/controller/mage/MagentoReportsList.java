package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MagentoReport;
import eu.shooktea.vmsm.view.controller.MainView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MagentoReportsList {

    @FXML private TableView<MagentoReport> exceptionTable;
    @FXML private Label timeLabel;

    private Magento magento;
    private VirtualMachine vm;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        magento = (Magento)Magento.getModuleByName("Magento");
        initColumns();
        initTableEvents();
        initLabel();
        exceptionTable.setItems(MagentoReport.allReports);
    }

    private void initColumns() {
        ObservableList<TableColumn<MagentoReport, ?>> columns = exceptionTable.getColumns();
        columns.clear();

        TableColumn<MagentoReport, LocalDateTime> date = new TableColumn<>("Time");
        date.setCellValueFactory(new PropertyValueFactory<>("time"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        date.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else {
                    setText(dateFormatter.format(item));
                }
            }
        });

        TableColumn<MagentoReport, String> text = new TableColumn<>("Text");
        text.setCellValueFactory(new PropertyValueFactory<>("message"));

        TableColumn<MagentoReport, String> name = new TableColumn<>("File name");
        name.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        columns.addAll(date, text, name);
    }

    private void initTableEvents() {
        exceptionTable.setRowFactory(tv -> {
            TableRow<MagentoReport> row = new TableRow<>();
            row.setOnMouseClicked(event -> MagentoReportStackTrace.openStackTraceWindow(row.getItem()));
            return row;
        });
    }

    private void initLabel() {
        MagentoReport.HoldTime time = MagentoReport.HoldTime.fromTime(MagentoReport.MAX_TIME_DIFFERENCE);
        if (time == MagentoReport.HoldTime.NEVER) timeLabel.setText("");
        else if (time == MagentoReport.HoldTime.ETERNITY) timeLabel.setText("From eternity");
        else timeLabel.setText("From the last " + time.toString());
    }

    public static void openMagentoReportsList(Object... lambdaArgs) {
        MagentoReport.notifyReports.clear();
        MainView.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/MagentoReportsList.fxml", "Exception reports", true);
    }
}
