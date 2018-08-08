package eu.shooktea.vmsm.view.controller.mysql;

import eu.shooktea.vmsm.module.mysql.TableContent;
import eu.shooktea.vmsm.module.mysql.TableEntry;
import eu.shooktea.vmsm.view.View;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public class TabScreen extends VBox {

    private @FXML TableView<TableEntry> dataTable;
    private @FXML ListView<TableEntry> tablesList;

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

    public static TabScreen showTabScreen(Object... lambdaArgs) {
        return View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/TabScreen.fxml", "Database");
    }
}
