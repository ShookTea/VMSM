package eu.shooktea.vmsm.module.ssh;

import com.jcraft.jsch.ChannelShell;
import eu.shooktea.fxtoolkit.terminal.TerminalConnection;
import eu.shooktea.fxtoolkit.terminal.TerminalIO;
import javafx.application.Platform;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Class representing single SSH connection.
 */
public class SshConnection extends TerminalConnection {
    private final ChannelShellIO io;

    SshConnection(ChannelShell shell) {
        this.io = new ChannelShellIO(shell);
        Console console = new Console();
        PrintStream stream = new PrintStream(console, true);
        this.io.setInputStream(stream);
    }

    @Override
    protected TerminalIO getTerminalIO() {
        return io;
    }

    private class Console extends OutputStream {
        public void write(int i) {
            Platform.runLater(() -> {
                print(String.valueOf((char)i));
                onTerminalUpdate.run();
            });
        }
    }
}