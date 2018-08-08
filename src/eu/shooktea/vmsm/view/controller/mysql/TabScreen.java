package eu.shooktea.vmsm.view.controller.mysql;

import eu.shooktea.vmsm.module.mysql.TableContent;
import eu.shooktea.vmsm.module.mysql.TableEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TabScreen extends VBox {

    private @FXML TableView<TableEntry> dataTable;

    public TabScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/eu/shooktea/vmsm/view/fxml/mysql/TabScreen.fxml"
        ));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    private TableContent content;
}
