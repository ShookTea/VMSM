package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.fxtoolkit.terminal.TerminalConnection;
import eu.shooktea.fxtoolkit.terminal.TerminalIO;
import eu.shooktea.vmsm.VM;

import java.io.PrintStream;

public class BashConnection extends TerminalConnection {

    public BashConnection(Service service) {
        this.terminalIO = new BashTerminalIO(service.getName(), VM.getOrThrow().getMainPath());
        Console console = new Console(this);
        PrintStream stream = new PrintStream(console, true);
        terminalIO.setInputStream(stream);
    }

    @Override
    protected TerminalIO getTerminalIO() {
        return terminalIO;
    }

    private final TerminalIO terminalIO;
}
