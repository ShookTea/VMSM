package eu.shooktea.vmsm.view.controller.mysql;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.MySQL;
import eu.shooktea.vmsm.module.SSH;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;

public class MysqlTerminal implements UserInfo, StageController {

    @FXML private TextArea output;
    @FXML private TextField input;

    private ChannelShell channel;
    private PrintStream printStream;
    private Queue<Character> inputStream = new ArrayDeque<>();

    private VirtualMachine vm;
    private SSH ssh;
    private MySQL mysql;

    @FXML
    private void initialize() {
        Console console = new Console(output);
        printStream = new PrintStream(console, true);
        try {
            vm = VM.getOrThrow();
            ssh = SSH.getModuleByName("SSH");
            mysql = MySQL.getModuleByName("MySQL");

            channel = (ChannelShell)ssh.openChannel(vm, this, "shell");
            if (channel == null) {
                output.setText("SSH is not configured or virtual machine is off.");
                return;
            }
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
            channel.connect(3000);

            new Thread(() -> {
                try {
                    String[] lines = createMysqlCommand(vm, mysql);
                    pout.write((lines[0] + '\n').getBytes());
                    if (lines[0].endsWith(" -p")) {
                        while (!output.getText().endsWith("Enter password: ")) {
                            Thread.sleep(10);
                        }
                        pout.write((lines[1] + '\n').getBytes());
                    }
                } catch (Throwable thr) {
                    thr.printStackTrace();
                    stage.close();
                }
            }).start();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    private static String[] createMysqlCommand(VirtualMachine vm, MySQL mysql) {
        String databaseName = mysql.getStringSetting(vm, "database");
        String user = mysql.getStringSetting(vm, "username");
        String pass = mysql.getStringSetting(vm, "password");

        String command = "mysql";
        if (databaseName != null && !databaseName.trim().isEmpty()) {
            command = command + " " + databaseName.trim();
        }
        if (user != null && !user.trim().isEmpty()) {
            command = command + " -u " + user.trim();
        }

        boolean usePass = pass != null && !pass.trim().isEmpty();

        if (!usePass) return new String[]{ command };
        else return new String[] { command + " -p", pass.trim() };
    }

    public static void openMysqlTerminal(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/MysqlTerminal.fxml", "MySQL Terminal", false);
    }

    @Override
    public String getPassphrase() {
        return JOptionPane.showInputDialog("MySQL terminal requested passphrase. Your passphrase: ");
    }

    @Override
    public String getPassword() {
        return JOptionPane.showInputDialog("MySQL terminal requested password. Your password: ");
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

    private class Console extends OutputStream {
        private TextArea output;

        public Console(TextArea ta) {
            this.output = ta;
        }

        public void write(int i) throws IOException {
            Platform.runLater(() -> {
                output.appendText(String.valueOf((char)i));
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace(printStream);
                    }
                    if (channel == null || channel.isClosed() || !channel.isConnected()) Platform.runLater(stage::close);

                }).start();
            });
        }
    }

    private Stage stage;
}
