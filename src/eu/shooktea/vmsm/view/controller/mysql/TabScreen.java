package eu.shooktea.vmsm.view.controller.mysql;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TabScreen extends VBox {

    private @FXML TableView<Object> dataTable;

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
}
