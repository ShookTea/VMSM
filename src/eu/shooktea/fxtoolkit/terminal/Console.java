package eu.shooktea.fxtoolkit.terminal;

import javafx.application.Platform;

import java.io.OutputStream;

public class Console extends OutputStream {

    public Console(TerminalConnection connection) {
        this.terminalConnection = connection;
    }

    public void write(int i) {
        Platform.runLater(() -> {
            terminalConnection.print(String.valueOf((char)i));
            terminalConnection.getOnTerminalUpdate().run();
        });
    }

    private final TerminalConnection terminalConnection;
}
