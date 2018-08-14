package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.fxtoolkit.terminal.Console;
import eu.shooktea.fxtoolkit.terminal.TerminalConnection;
import eu.shooktea.fxtoolkit.terminal.TerminalIO;
import eu.shooktea.vmsm.VirtualMachine;

import java.io.PrintStream;

public class BashConnection extends TerminalConnection {

    public BashConnection(Service service, VirtualMachine vm) {
        this.terminalIO = new BashTerminalIO(service.getName(), vm.getMainPath());
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
