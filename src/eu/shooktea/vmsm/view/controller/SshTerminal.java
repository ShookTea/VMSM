package eu.shooktea.vmsm.view.controller;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;
import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.SSH;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class SshTerminal implements UserInfo, StageController {

    @FXML private TextArea output;
    @FXML private TextField input;

    private ChannelShell channel;
    private PrintStream printStream;

    @FXML
    private void initialize() {
        Console console = new Console(output);
        printStream = new PrintStream(console, true);
        try {
            VirtualMachine vm = Start.virtualMachineProperty.get();
            SSH ssh = (SSH)SSH.getModuleByName("SSH");
            channel = (ChannelShell)ssh.openChannel(vm, this, "shell");
            channel.setOutputStream(printStream);
        } catch (JSchException e) {
            e.printStackTrace(printStream);
        }
    }

    @FXML
    private void writeInput() {

    }

    public static void openSshTerminal(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/SshTerminal.fxml", "SSH Terminal", false);
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
        Object[] options={ "yes", "no" };
        int foo=JOptionPane.showOptionDialog(null,
                message,
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        return foo==0;
    }

    @Override
    public void showMessage(String message) {
        printStream.println(message);
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(e -> {
            if (channel != null) {
                try {
                    channel.disconnect();
                    channel.getSession().disconnect();
                } catch (JSchException e1) {
                    e1.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }

    private Stage stage;

    private class Console extends OutputStream {
        private TextArea output;

        public Console(TextArea ta) {
            this.output = ta;
        }

        public void write(int i) throws IOException {
            output.appendText(String.valueOf((char)i));
        }
    }
}
