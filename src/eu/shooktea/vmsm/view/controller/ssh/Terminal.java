package eu.shooktea.vmsm.view.controller.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.ssh.SSH;
import eu.shooktea.vmsm.module.ssh.SshConnection;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;

public class Terminal implements UserInfo {
    @FXML private WebView view;
    private WebEngine engine;
    private SshConnection connection;
    private SSH ssh;
    private VirtualMachine vm;

    @FXML
    private void initialize() {
        try {
            engine = view.getEngine();
            view.requestFocus();
            ssh = SSH.getModuleByName("SSH");
            vm = VM.getOrThrow();
            connection = ssh.createConnection(vm, this);
            reloadContent();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void keyEvent(KeyEvent event) {
        connection.keyTyped(event);
        reloadContent();
    }

    private void reloadContent() {
        engine.loadContent(HTML_OPEN + connection.getAsHtml() + HTML_CLOSE);
    }

    private static final String HTML_STYLE =
            "background-color: black; color: white; font-family: monospaced; font-size: 13px;";
    private static final String HTML_OPEN =
            "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='" + HTML_STYLE + "'>";
    private static final String HTML_CLOSE = "</body></html>";

    public static void openTerminal(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/ssh/Terminal.fxml", "SSH terminal");
    }

    @Override
    public String getPassphrase() {
        return JOptionPane.showInputDialog("SSH terminal requested passphrase. Your passphrase: ");
    }

    @Override
    public String getPassword() {
        return JOptionPane.showInputDialog("SSH terminal requested password. Your password: ");
    }

    @Override
    public boolean promptPassword(String message) {
        return promptYesNo(message);
    }

    @Override
    public boolean promptPassphrase(String message) {
        return promptYesNo(message);
    }

    @Override
    public boolean promptYesNo(String message) {
        if (message.replace('\n', ' ').replace('\r', ' ').matches(fingerprintSsh)) {
            Boolean auto = (Boolean)ssh.getSetting(vm, "auto_fingerprints");
            if (auto == null || auto)
                return true;
        }
        Object[] options={ "Yes", "No" };
        int option = JOptionPane.showOptionDialog(null,
                message,
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        return option==0;
    }

    private static final String fingerprintSsh = "^The authenticity of host '[^']*' can't be established\\..*Are you sure you want to continue connecting\\?$";

    @Override
    public void showMessage(String message) {
        connection.println(message);
    }
}
