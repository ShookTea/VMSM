package eu.shooktea.fxtoolkit.terminal;

import eu.shooktea.vmsm.module.ssh.DefaultUserInfo;


public class UserInfo implements DefaultUserInfo {
    public UserInfo(TerminalConnection connection) {
        this.connection = connection;
    }

    public void setConnection(TerminalConnection connection) {
        this.connection = connection;
    }

    @Override
    public void showMessage(String message) {
        connection.println(message);
    }

    private TerminalConnection connection;
}
