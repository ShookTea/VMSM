package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.view.ScreenManager;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Config implements StageController {
    @FXML private ChoiceBox<Screen> displayScreen;

    private Stage stage;

    @FXML
    private void initialize() {
        displayScreen.setItems(ScreenManager.screens);
        displayScreen.setConverter(ScreenManager.converter);
        displayScreen.setValue(ScreenManager.getCurrentScreen());
    }

    @FXML
    private void cancel() {
        stage.close();
    }

    @FXML
    private void save() {
        ScreenManager.setScreen(displayScreen.getValue());
        Storage.saveAll();
        stage.close();
    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/Config.fxml", "Settings");
    }
}
