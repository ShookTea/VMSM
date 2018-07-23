package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SshTerminal {

    @FXML private TextArea output;
    @FXML private TextField input;

    @FXML
    private void initialize() {

    }

    @FXML
    private void writeInput() {

    }

    public static void openSshTerminal(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/SshTerminal.fxml", "SSH Terminal", false);
    }
}
