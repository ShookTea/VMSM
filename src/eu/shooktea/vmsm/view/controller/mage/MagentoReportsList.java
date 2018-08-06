package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.mage.Magento;
import eu.shooktea.vmsm.module.mage.Report;
import eu.shooktea.vmsm.view.View;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MagentoReportsList {

    @FXML private TableView<Report> exceptionTable;
    @FXML private Label timeLabel;

    private Magento magento;
    private VirtualMachine vm;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        magento = Magento.getModuleByName("Magento");
        initColumns();
        initTableEvents();
        initLabel();
        exceptionTable.setItems(Report.allReports);
    }

    private void initColumns() {
        ObservableList<TableColumn<Report, ?>> columns = exceptionTable.getColumns();
        columns.clear();

        TableColumn<Report, LocalDateTime> date = new TableColumn<>("Time");
        date.setCellValueFactory(new PropertyValueFactory<>("time"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        date.setCellFactory(column -> new TableCell<Report, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else {
                    setText(dateFormatter.format(item));
                }
            }
        });

        TableColumn<Report, String> text = new TableColumn<>("Text");
        text.setCellValueFactory(new PropertyValueFactory<>("message"));
        columns.addAll(date, text);
    }

    private void initTableEvents() {
        exceptionTable.setRowFactory(tv -> {
            TableRow<Report> row = new TableRow<>();
            row.setOnMouseClicked(event -> MagentoReportStackTrace.openStackTraceWindow(row.getItem()));
            return row;
        });
    }

    private void initLabel() {
        Report.HoldTime time = Report.HoldTime.fromTime(Report.MAX_TIME_DIFFERENCE);
        if (time == Report.HoldTime.NEVER) timeLabel.setText("");
        else if (time == Report.HoldTime.ETERNITY) timeLabel.setText("From eternity");
        else timeLabel.setText("From the last " + time.toString());
    }

    public static void openMagentoReportsList(Object... lambdaArgs) {
        Report.notifyReports.clear();
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/MagentoReportsList.fxml", "Exception reports");
    }
}
