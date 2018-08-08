package eu.shooktea.vmsm.view.controller.mysql;

import eu.shooktea.vmsm.module.mysql.TableContent;
import eu.shooktea.vmsm.module.mysql.TableEntry;
import eu.shooktea.vmsm.view.View;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;


public class TabScreen extends VBox {

    private @FXML TableView<TableEntry> dataTable;

    public void setDataTableContent(TableContent content) {
        this.content = content;
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

    public TableContent getDataTableContent() {
        return content;
    }

    public static TabScreen showTabScreen(Object... lambdaArgs) {
        return View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/TabScreen.fxml", "Database");
    }

    private TableContent content;
}
