package eu.shooktea.vmsm.view.controller.ssh;

import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Terminal {
    @FXML private WebView view;
    private WebEngine engine;

    @FXML
    private void initialize() {
        engine = view.getEngine();
        createContent("OOO<br/>iii");
        view.requestFocus();
    }

    private void createContent(String content) {
        engine.loadContent(HTML_OPEN + content + HTML_CLOSE);
    }

    private static final String HTML_STYLE =
            "background-color: black; color: white; font-family: monospaced; font-size: 13px;";
    private static final String HTML_OPEN =
            "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='" + HTML_STYLE + "'>";
    private static final String HTML_CLOSE = "</body></html>";

    public static void openTerminal(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/ssh/Terminal.fxml", "SSH terminal");
    }
}
