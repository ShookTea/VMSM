package eu.shooktea.vmsm.module.ssh;

import com.jcraft.jsch.*;
import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.view.controller.ssh.SshConfig;
import eu.shooktea.vmsm.view.controller.ssh.SshTerminal;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Module representing SSH connection with VM.
 */
public class SSH extends VMModule {
    @Override
    public String getName() {
        return "SSH";
    }

    @Override
    public String getDescription() {
        return "SSH terminal client";
    }

    @Override
    public Optional<Runnable> openConfigWindow() {
        return Optional.of(SshConfig::openSshConfigWindow);
    }

    /**
     * Opens new channel of SSH communication protocol.
     * @param vm virtual machine
     * @param ui user info
     * @param type type of connection, i.e. "shell"
     * @return new channel of communication or {@code null} if connection timed out.
     * @throws JSchException if anything wrong happens during connection
     */
    public Channel openChannel(VirtualMachine vm, UserInfo ui, String type) throws JSchException {
        String user = getStringSetting(vm, "user");
        String passwd = getStringSetting(vm, "password");
        String host = getStringSetting(vm, "host");
        if (user == null || passwd == null || host == null) return null;

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(passwd);
        session.setUserInfo(ui);
        try {
            session.connect(5000);
        }
        catch (JSchException ex) {
            return null;
        }
        Channel channel = session.openChannel(type);
        return channel;
    }

    /**
     * Creates new connection to shell.
     * @param vm virtual machine
     * @param ui user info
     * @return connection to shell via SSH
     * @throws JSchException if anything wrong happens during connection
     */
    public SshConnection createConnection(VirtualMachine vm, UserInfo ui) throws JSchException {
        ChannelShell shell = (ChannelShell)openChannel(vm, ui, "shell");
        return new SshConnection(shell);
    }

    @Override
    public List<ImageView> getQuickGuiButtons() {
        ImageView openTerminal = Toolkit.createQuickGuiButton("terminal.png", "Open SSH terminal");
        openTerminal.setOnMouseClicked(SshTerminal::openSshTerminal);
        return Collections.singletonList(openTerminal);
    }

    @Override
    public Optional<MenuItem> getMenuItem() {
        Menu root = new Menu("SSH", Toolkit.createMenuImage("terminal.png"));

        MenuItem openTerminal = new MenuItem("Open terminal...");
        openTerminal.setOnAction(SshTerminal::openSshTerminal);

        MenuItem config = new MenuItem("SSH configuration...", Toolkit.createMenuImage("run.png"));
        config.setOnAction(SshConfig::openSshConfigWindow);

        root.getItems().addAll(openTerminal, config);
        return Optional.of(root);
    }

    @Override
    public int getSortValue() {
        return 1000;
    }
}
