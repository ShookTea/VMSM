package eu.shooktea.vmsm.module.ssh;

import javax.swing.*;

public interface DefaultUserInfo extends com.jcraft.jsch.UserInfo {
    @Override
    public default String getPassphrase() {
        return JOptionPane.showInputDialog("SSH terminal requested passphrase. Your passphrase: ");
    }

    @Override
    public default String getPassword() {
        return JOptionPane.showInputDialog("SSH terminal requested password. Your password: ");
    }

    @Override
    public default boolean promptPassword(String message) {
        return promptYesNo(message);
    }

    @Override
    default boolean promptPassphrase(String message) {
        return promptYesNo(message);
    }
}
