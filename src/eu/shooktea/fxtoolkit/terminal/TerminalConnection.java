package eu.shooktea.fxtoolkit.terminal;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class TerminalConnection {
    protected abstract TerminalIO getTerminalIO();

    public void init() {
        getTerminalIO().init();
        this.stream = new PrintStream(getTerminalIO().getTerminalInputStream(), true);
        this.pout = getTerminalIO().getTerminalOutputStream();
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

    public String getConsoleContent() {
        return consoleDisplay + input;
    }

    public void close() {
        getTerminalIO().close();
    }

    public void setOnTerminalUpdate(Runnable runnable) {
        this.onTerminalUpdate = runnable;
    }

    public Runnable getOnTerminalUpdate() {
        return this.onTerminalUpdate;
    }

    private OutputStream pout;
    private PrintStream stream;
    private String consoleDisplay = "";
    private String input = "";
    private boolean executingCommand = false;
    private Runnable onTerminalUpdate = () -> {};
    private List<String> commandLineHistory = new ArrayList<>();
    private int currentHistoryPos = -1;
}