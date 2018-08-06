package eu.shooktea.vmsm.view.controller;

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

    @FXML
    private void initialize() {
        ObservableList<Screen> screens = Screen.getScreens();
        displayScreen.setItems(screens);
        screenMap = new HashMap<>();
        StringConverter<Screen> converter = new StringConverter<Screen>() {
            @Override
            public String toString(Screen object) {
                return "Screen " + (screens.indexOf(object) + 1);
            }

            @Override
            public Screen fromString(String string) {
                return screenMap.get(string);
            }
        };
        for (Screen screen : screens) {
            screenMap.put(converter.toString(screen), screen);
        }
        displayScreen.setConverter(converter);
    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/Config.fxml", "Settings");
    }
}
