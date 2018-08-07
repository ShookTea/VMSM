package eu.shooktea.vmsm.module.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;

import java.io.*;

/**
 * Class representing single SSH connection.
 */
public class SshConnection {
    SshConnection(ChannelShell shell) {
        this.shell = shell;
    }

    /**
     * Initializes SSH connection.
     * @throws IOException when piping input stream has failed
     * @throws JSchException when connection to SSH has failed
     */
    public void init() throws IOException, JSchException {
        if (shell == null) return;
        shell.setAgentForwarding(true);
        Console console = new Console();
        shell.setOutputStream(new PrintStream(console, true));

        PipedInputStream pin = new PipedInputStream();
        pout = new PipedOutputStream(pin);
        shell.setInputStream(pin);

        shell.connect(300);
    }

    public void keyTyped(KeyEvent event) {
        System.out.println("KeyCode = " + event.getCode() + ", Character = " + event.getCharacter());
    }

    public void println(String text) {
        consoleDisplay += text + "\n";
    }

    public String getAsHtml() {
        return consoleDisplay;
    }

    private final ChannelShell shell;
    private PipedOutputStream pout;
    private String consoleDisplay = "";
    private String input = "";

    private class Console extends OutputStream {
        public void write(int i) {
            Platform.runLater(() -> {
                consoleDisplay += String.valueOf((char)i);
            });
        }
    }
}
