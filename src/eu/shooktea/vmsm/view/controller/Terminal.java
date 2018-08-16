package eu.shooktea.vmsm.view.controller;

import eu.shooktea.fxtoolkit.terminal.TerminalConnection;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.ssh.SSH;
import eu.shooktea.vmsm.view.StageController;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.function.Function;

public class Terminal implements StageController {
    @FXML private WebView view;
    private WebEngine engine;
    private TerminalConnection connection;

    @FXML
    private void initialize() {
        try {
            engine = view.getEngine();
            view.requestFocus();
            connection = connectionSupplier.apply(VM.getOrThrow());
            connection.setOnTerminalUpdate(this::reloadContent);
            connection.init();
            reloadContent();
        } catch (Exception e) {
            connection.println(e);
        }
    }

    @FXML
    private void keyEvent(KeyEvent event) {
        connection.keyTyped(event);
    }

    private void reloadContent() {
        String consoleContent = connection.getConsoleContent();
        String htmlContent = SSH.sshToHtml(consoleContent);
        engine.loadContent(htmlContent);
    }

    @Override
    public void setStage(Stage stage) {
        stage.setOnCloseRequest(e -> connection.close());
    }

    public static void openTerminal(String windowTitle, Function<VirtualMachine, TerminalConnection> connectionSupplier) {
        Terminal.connectionSupplier = connectionSupplier;
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/Terminal.fxml", windowTitle);
    }

    private static Function<VirtualMachine, TerminalConnection> connectionSupplier;
}
