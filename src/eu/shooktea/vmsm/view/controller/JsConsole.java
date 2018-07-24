package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.view.View;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class JsConsole {
    @FXML private TextField input;
    @FXML private TextArea output;

    @FXML
    private void initialize() {
        output.textProperty().bind(outputText);
        input.setOnAction(e -> {
            String command = input.getText();
            input.clear();
            View.controller().browser.executeJavaScript(command);
        });
    }

    public static final StringProperty outputText = new SimpleStringProperty("");

    public static void openJsConsole(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/JsConsole.fxml", "JS console", false);
    }
}
