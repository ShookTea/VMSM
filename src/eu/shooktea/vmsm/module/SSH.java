package eu.shooktea.vmsm.module;

import com.jcraft.jsch.*;
import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.ssh.SshConfig;
import eu.shooktea.vmsm.view.controller.ssh.SshTerminal;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SSH extends Module {
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

    @Override
    public void afterModuleRemoved() {
        super.afterModuleRemoved();
        View.controller().toolBar.getItems().removeAll(toolbarElements);
    }

    @Override
    public void afterModuleTurnedOff() {
        super.afterModuleTurnedOff();
        Platform.runLater(() -> {
            View.controller().toolBar.getItems().removeAll(toolbarElements);
        });
    }

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

    @Override
    public void reloadToolbar() {
        View.controller().toolBar.getItems().addAll(toolbarElements);
    }

    private List<Node> createToolbarElements() {
        ImageView openTerminal = Toolkit.createToolbarImage("terminal.png");
        Tooltip removeCacheTip = new Tooltip("Open SSH terminal");
        Tooltip.install(openTerminal, removeCacheTip);
        openTerminal.setOnMouseClicked(SshTerminal::openSshTerminal);

        return Arrays.asList(
                new Separator(Orientation.VERTICAL),
                openTerminal
        );
    }

    private List<Node> toolbarElements = createToolbarElements();
}
