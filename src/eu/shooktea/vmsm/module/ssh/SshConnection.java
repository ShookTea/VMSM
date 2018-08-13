package eu.shooktea.vmsm.module.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import eu.shooktea.fxtoolkit.terminal.TerminalConnection;
import eu.shooktea.fxtoolkit.terminal.TerminalIO;
import javafx.application.Platform;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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

    private static class ChannelShellIO implements TerminalIO {
        private ChannelShell shell;
        private OutputStream os;
        private OutputStream is;

        public ChannelShellIO(ChannelShell shell) {
            this.shell = shell;
        }

        @Override
        public void init() {
            try {
                if (shell == null) return;
                shell.setAgentForwarding(true);
                shell.setOutputStream(os);
                shell.setPtySize(200, 500, 800, 600);

                PipedInputStream pin = new PipedInputStream();
                is = new PipedOutputStream(pin);
                shell.setInputStream(pin);

                shell.connect(300);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public void setInputStream(OutputStream stream) {
            this.os = stream;
        }

        @Override
        public void close() {
            try {
                if (shell.isClosed()) return;
                shell.disconnect();
                shell.getSession().disconnect();
            } catch (JSchException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public OutputStream getTerminalOutputStream() {
            return is;
        }

        @Override
        public OutputStream getTerminalInputStream() {
            return os;
        }
    }
}