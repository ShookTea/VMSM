package eu.shooktea.vmsm.module.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        stream = new PrintStream(console, true);
        shell.setOutputStream(stream);
        shell.setPtySize(200, 500, 800, 600);

        PipedInputStream pin = new PipedInputStream();
        pout = new PipedOutputStream(pin);
        shell.setInputStream(pin);

        shell.connect(300);
    }

    public void keyTyped(KeyEvent event) {
        if (executingCommand) return;
        if (event.getCode() == KeyCode.UNDEFINED) {
            String character = event.getCharacter();
            if (character.equals("\r") || character.equals("\n")) {
                pushCommand();
            }
            else if (character.equals(" ") || character.equals("\t")) {
                input += character;
                onTerminalUpdate.run();
            }
            else {
                input += character.trim();
                onTerminalUpdate.run();
            }
        }
        else if (event.getCode() == KeyCode.BACK_SPACE && input.length() > 0) {
            input = input.substring(0, input.length() - 1);
            onTerminalUpdate.run();
        }
        else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            if (currentHistoryPos == -1) currentHistoryPos = commandLineHistory.size();
            if (event.getCode() == KeyCode.UP) currentHistoryPos--;
            else currentHistoryPos++;
            currentHistoryPos = Math.min(currentHistoryPos, commandLineHistory.size());
            currentHistoryPos = Math.max(currentHistoryPos, 0);
            if (currentHistoryPos == commandLineHistory.size()) input = "";
            else input = commandLineHistory.get(currentHistoryPos);
            onTerminalUpdate.run();
        }
    }

    public void print(String text) {
        consoleDisplay += text;
        if (consoleDisplay.endsWith("\u001B[H\u001B[J")) consoleDisplay = "";
        onTerminalUpdate.run();
    }

    public void println(String text) {
        print(text + "\n");
    }

    public void println(Throwable thr) {
        thr.printStackTrace(stream);
    }

    private void pushCommand() {
        String command = input.trim();
        input = "";
        if (command.isEmpty()) return;
        commandLineHistory.add(command);
        currentHistoryPos = -1;

        executingCommand = true;
        byte[] line = command.getBytes();
        try {
            pout.write(line, 0, line.length);
            pout.write('\n');
        } catch (IOException e1) {
            e1.printStackTrace(stream);
        }
        executingCommand = false;
    }

    public String getAsHtml() {
        String toRet = consoleDisplay + input;
        toRet = toRet
                .replaceAll("\\e\\[([0-9]+)m", "</span><span class='bash_$1'>")
                .replaceAll("\\e\\[([0-9]+);([0-9]+)m", "</span><span class='bash_$1 bash_$2'>")
                .replaceAll("\\n", "<br/>")
                ;
        toRet = "<span>" + toRet + "</span>";
        return SshHtmlConverter.sshToHtml(toRet);
    }

    public void close() throws JSchException {
        if (shell.isClosed()) return;
        shell.disconnect();
        shell.getSession().disconnect();
    }

    public void setOnTerminalUpdate(Runnable runnable) {
        this.onTerminalUpdate = runnable;
    }

    private final ChannelShell shell;
    private PipedOutputStream pout;
    private PrintStream stream;
    private String consoleDisplay = "";
    private String input = "";
    private boolean executingCommand = false;
    private Runnable onTerminalUpdate = () -> {};
    private List<String> commandLineHistory = new ArrayList<>();
    private int currentHistoryPos = -1;

    private class Console extends OutputStream {
        public void write(int i) {
            Platform.runLater(() -> {
                print(String.valueOf((char)i));
                onTerminalUpdate.run();
            });
        }
    }

    public static final String styles
            = ".bash_0 {}"
            + ".bash_1, .bash_01 {font-weight: bold;}"
            + ".bash_4, .bash_04 {text-decoration: underline;}"
            + ".bash_8, .bash_08 {display: none;}"
            + color(30, "black")
            + color(31, "red")
            + color(32, "green")
            + color(33, "yellow")
            + color(34, "blue")
            + color(35, "magenta")
            + color(36, "cyan")
            + color(37, "lightgrey")
            + color(90, "darkgrey")
            + color(91, "red")
            + color(92, "green")
            + color(93, "yellow")
            + color(94, "blue")
            + color(95, "magenta")
            + color(96, "cyan")
            ;

    private static String color(int code, String color) {
        return ".bash_" + code + " {color: " + color + ";}";
    }
}