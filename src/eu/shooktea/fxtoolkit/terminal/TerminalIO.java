package eu.shooktea.fxtoolkit.terminal;

import java.io.OutputStream;

public interface TerminalIO {
    void init();
    void close();
    OutputStream getTerminalOutputStream();
    OutputStream getTerminalInputStream();
}
