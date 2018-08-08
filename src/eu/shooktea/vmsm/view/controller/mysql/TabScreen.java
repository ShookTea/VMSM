package eu.shooktea.vmsm.view.controller.mysql;

import com.jcraft.jsch.JSchException;
import eu.shooktea.vmsm.module.mysql.MySQL;
import eu.shooktea.vmsm.module.mysql.SqlConnection;
import eu.shooktea.vmsm.module.mysql.TableContent;
import eu.shooktea.vmsm.module.mysql.TableEntry;
import eu.shooktea.vmsm.view.StageController;
import eu.shooktea.vmsm.view.View;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;


public class TabScreen implements StageController {

    private @FXML TableView<TableEntry> dataTable;
    private @FXML ListView<TableEntry> tablesList;

    private SqlConnection connection;

    @FXML
    private void initialize() {
        MySQL sql = MySQL.getModuleByName("MySQL");
        connection = sql.createConnection();
        try {
            connection.open();
        } catch (JSchException | SQLException e) {
            e.printStackTrace();
            requestStageClose();
            return;
        }
    }

    public void setDataTableContent(TableContent content) {
        ObservableList<TableColumn<TableEntry, ?>> columns = dataTable.getColumns();
        columns.clear();
        for (int i = 0; i < content.getColumnCount(); i++) {
            final int index = i;
            TableColumn<TableEntry, String> column = new TableColumn<>(content.getColumnNameAt(i));
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValueAt(index)));
            columns.add(column);
        }
        dataTable.setItems(content.getRows());
    }

    public void setTablesListContent(TableContent content) {
        tablesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tablesList.setCellFactory(lv -> new ListCell<TableEntry>() {
            @Override
            protected void updateItem(TableEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    String name = item.getValueAt(0);
                    String count = item.getValueAt(1);
                    String rows = "(" + count + " " + (count.equals("1") ? "row" : "rows") + ")";
                    setText(name + " " + rows);
                }
            }
        });
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(e -> {
            try {
                if (connection.isOpen()) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void requestStageClose() {
        try {
            if (connection.isOpen())  connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (stage != null) stage.close();
        else new Thread(() -> {
            while (stage == null)  try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(stage::close);
        }).start();
    }

    private Stage stage = null;

    public static TabScreen showTabScreen(Object... lambdaArgs) {
        return View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/TabScreen.fxml", "Database");
    }
}
