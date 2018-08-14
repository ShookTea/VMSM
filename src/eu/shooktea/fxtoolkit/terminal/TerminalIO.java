package eu.shooktea.fxtoolkit.terminal;

import java.io.OutputStream;

public interface TerminalIO {
    void init();
    void close();
    default void setInputStream(OutputStream os) {}
    OutputStream getTerminalOutputStream();
    OutputStream getTerminalInputStream();
}
