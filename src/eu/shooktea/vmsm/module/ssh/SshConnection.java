package eu.shooktea.vmsm.module.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
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
        if (event.getCode() == KeyCode.UNDEFINED) {
            String character = event.getCharacter();
            if (character.equals("\r") || character.equals("\n")) {
                print("\n");
            }
            else if (character.equals(" ") || character.equals("\t")) {
                input += character;
            }
            else {
                input += character.trim();
            }
        }
        else if (event.getCode() == KeyCode.BACK_SPACE && input.length() > 0) {
            input = input.substring(0, input.length() - 1);
        }
    }

    public void print(String text) {
        consoleDisplay += text;
    }

    public void println(String text) {
        print(text + "\n");
    }

    public String getAsHtml() {
        String toRet = consoleDisplay + input;
        return toRet.replaceAll("\\n", "<br/>");
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
