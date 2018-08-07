package eu.shooktea.vmsm.module.ssh;

import com.jcraft.jsch.ChannelShell;

/**
 * Class representing single SSH connection.
 */
public class SshConnection {
    SshConnection(ChannelShell shell) {
        this.shell = shell;
    }

    /**
     * Initializes SSH connection.
     */
    public void init() {
        if (shell == null) return;

    }

    private final ChannelShell shell;
}
