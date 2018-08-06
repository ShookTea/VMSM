package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.view.View;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class Config implements StageController {
    @FXML private ChoiceBox<Screen> displayScreen;

    private Map<String, Screen> screenMap;
    private Stage stage;
    private ObservableList<Screen> screens;

    private final StringConverter<Screen> screenToString = new StringConverter<Screen>() {
        @Override
        public String toString(Screen object) {
            return "Screen " + (screens.indexOf(object) + 1);
        }

        @Override
        public Screen fromString(String string) {
            return screenMap.get(string);
        }
    };

    @FXML
    private void initialize() {
        screens = Screen.getScreens();
        displayScreen.setItems(screens);
        screenMap = new HashMap<>();

        for (Screen screen : screens) {
            screenMap.put(screenToString.toString(screen), screen);
        }
        displayScreen.setConverter(screenToString);
        Screen val = screenToString.fromString((String)Storage.config.get("screen"));
        if (val == null) val = screens.get(0);
        displayScreen.setValue(val);
    }

    @FXML
    private void cancel() {
        stage.close();
    }

    @FXML
    private void save() {
        Storage.config.put("screen", screenToString.toString(displayScreen.getValue()));
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
