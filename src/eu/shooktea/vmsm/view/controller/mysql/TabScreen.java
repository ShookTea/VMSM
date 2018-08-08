package eu.shooktea.vmsm.view.controller.mysql;

import com.jcraft.jsch.JSchException;
import com.mysql.cj.xdevapi.Table;
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

import java.sql.SQLException;


public class TabScreen implements StageController {

    private @FXML Tab dataTab;
    private @FXML TableView<TableEntry> dataTable;
    private @FXML ListView<TableEntry> tablesList;
    private @FXML TextArea selectFilters;
    private @FXML Spinner<Integer> offsetSpinner;
    private @FXML Spinner<Integer> limitSpinner;

    private @FXML TextArea queryField;
    private @FXML TableView<TableEntry> availableFieldsTable;

    private SqlConnection connection;

    @FXML
    private void initialize() {
        offsetSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        limitSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 300));
        dataTable.setPlaceholder(new Label("No table selected"));
        MySQL sql = MySQL.getModuleByName("MySQL");
        connection = sql.createConnection();
        try {
            connection.open();
            String query =
                    "SELECT `TABLE_NAME`, `TABLE_ROWS` FROM `information_schema`.`TABLES` WHERE `TABLE_SCHEMA`='energa'";
            setTablesListContent(new TableContent(connection.query(query)));
        } catch (JSchException | SQLException e) {
            e.printStackTrace();
            requestStageClose();
            return;
        }

        tablesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            String tableName = newValue.getValueAt(0);
            selectFilters.clear();
            offsetSpinner.getValueFactory().setValue(0);
            limitSpinner.getValueFactory().setValue(300);
            String query = "SELECT * FROM `" + tableName + "` LIMIT 300";
            String query2 = "DESCRIBE `" + tableName + "`";
            setTableQuery(dataTable, query);
            setTableQuery(availableFieldsTable, query2, false);
        });
    }

    @FXML
    private void applyFilters() {
        TableEntry selectedItem = tablesList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        String where = selectFilters.getText().trim();
        int offset = offsetSpinner.getValue();
        int limit = limitSpinner.getValue();

        String query = "SELECT * FROM (" + queryField.getText() + ") AS `main_table`";
        if (!where.isEmpty()) {
            query += " WHERE " + where;
        }
        query += " LIMIT " + offset + ", " + limit;
        setTableQuery(dataTable, query, false);
    }

    @FXML
    private void runQueryField() {
        setTableQuery(dataTable, queryField.getText(), false);
    }

    private void setTableQuery(TableView<TableEntry> table, String query) {
        setTableQuery(table, query, true);
    }

    private void setTableQuery(TableView<TableEntry> table, final String query, boolean setInQueryWindow) {
        setTableContent(table, null);
        table.setPlaceholder(new Label("Loading data..."));
        if (setInQueryWindow) queryField.setText(query);
        new Thread(() -> {
            try {
                TableContent content = new TableContent(connection.query(query));
                Platform.runLater(() -> {
                    setTableContent(table, content);
                    table.setPlaceholder(new Label("Table is empty."));
                    dataTab.getTabPane().getSelectionModel().select(dataTab);
                });
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(this::requestStageClose);
            }
        }).start();
    }

    private void setTableContent(TableView<TableEntry> table, TableContent content) {
        if (content == null) {
            table.getItems().clear();
            table.getColumns().clear();
            return;
        }
        ObservableList<TableColumn<TableEntry, ?>> columns = table.getColumns();
        columns.clear();
        for (int i = 0; i < content.getColumnCount(); i++) {
            final int index = i;
            TableColumn<TableEntry, String> column = new TableColumn<>(content.getColumnNameAt(i));
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValueAt(index)));
            columns.add(column);
        }
        table.setItems(content.getRows());
    }

    private void setTablesListContent(TableContent content) {
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
        tablesList.setItems(content.getRows());
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
