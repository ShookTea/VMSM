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
import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class SshTerminal implements UserInfo, StageController {

    @FXML private TextArea output;
    @FXML private TextField input;

    private ChannelShell channel;
    private PrintStream printStream;
    private Queue<Character> inputStream = new ArrayDeque<>();

    @FXML
    private void initialize() {
        Console console = new Console(output);
        printStream = new PrintStream(console, true);
        try {
            VirtualMachine vm = Start.virtualMachineProperty.get();
            SSH ssh = (SSH)SSH.getModuleByName("SSH");
            channel = (ChannelShell)ssh.openChannel(vm, this, "shell");
            channel.setAgentForwarding(true);
            channel.setPtyType("vt102");
            channel.setOutputStream(printStream);

            PipedInputStream pin = new PipedInputStream();
            PipedOutputStream pout = new PipedOutputStream(pin);

            input.setOnAction(e -> {
                byte[] line = input.getText().getBytes();
                input.clear();
                try {
                    pout.write(line, 0, line.length);
                    pout.write('\n');
                } catch (IOException e1) {
                    e1.printStackTrace(printStream);
                }
            });

            channel.setInputStream(pin);
            channel.setOutputStream(System.out);
            channel.connect(3000);
        } catch (JSchException e) {
            e.printStackTrace(printStream);
        } catch (IOException e) {
            e.printStackTrace(printStream);
        }
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
        Object[] options={ "Yes", "No" };
        int option = JOptionPane.showOptionDialog(null,
                message,
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        return option==0;
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
