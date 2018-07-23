package eu.shooktea.vmsm.module;

import com.jcraft.jsch.*;
import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.controller.SshTerminal;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.List;

public class SSH extends Module {
    @Override
    public String getName() {
        return "SSH";
    }

    @Override
    public String getDescription() {
        return "SSH client";
    }

    @Override
    public void afterModuleRemoved() {
        super.afterModuleRemoved();
        Start.mainWindow.toolBar.getItems().removeAll(toolbarElements);
    }

    @Override
    public void afterModuleTurnedOff() {
        super.afterModuleTurnedOff();
        Platform.runLater(() -> {
            Start.mainWindow.toolBar.getItems().removeAll(toolbarElements);
        });
    }

    public Channel openChannel(VirtualMachine vm, UserInfo ui, String type) throws JSchException {
        JSch jsch = new JSch();
        String user = "vagrant";
        String passwd = "vagrant";
        String host = "sklep.energa.dev";
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(passwd);
        session.setUserInfo(ui);
        session.connect(30000);
        Channel channel = session.openChannel(type);
        return channel;
    }

    @Override
    public void reloadToolbar() {
        Start.mainWindow.toolBar.getItems().addAll(toolbarElements);
    }

    private List<Node> createToolbarElements() {
        ImageView openTerminal = Start.createToolbarImage("terminal.png");
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