package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.view.ScreenManager;
import eu.shooktea.vmsm.view.StageController;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
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
        Label alertContent = new Label("Changes to configuration has been saved. You need to restart application to apply them.");
        alertContent.setWrapText(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuration");
        alert.setHeaderText("Configuration saved");
        alert.getDialogPane().setMinHeight(Region.USE_COMPUTED_SIZE);
        alert.getDialogPane().setPrefHeight(Region.USE_COMPUTED_SIZE);
        alert.getDialogPane().setMaxHeight(Region.USE_COMPUTED_SIZE);
        alert.getDialogPane().setContent(alertContent);
        alert.show();
    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/Config.fxml", "Settings");
    }
}
