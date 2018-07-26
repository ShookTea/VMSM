package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.mysql.MysqlConfig;
import eu.shooktea.vmsm.view.controller.mysql.MysqlTerminal;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQL extends Module {
    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public String getDescription() {
        return "MySQL client via SSH";
    }

    @Override
    public Optional<Runnable> openConfigWindow() {
        return Optional.of(MysqlConfig::openMysqlConfigWindow);
    }

    @Override
    public void afterModuleRemoved() {
        super.afterModuleRemoved();
        if (toolbarElements == null) toolbarElements = createToolbarElements();
        View.controller().toolBar.getItems().removeAll(toolbarElements);
    }

    @Override
    public void afterModuleTurnedOff() {
        super.afterModuleTurnedOff();
        if (toolbarElements == null) toolbarElements = createToolbarElements();
        Platform.runLater(() -> {
            View.controller().toolBar.getItems().removeAll(toolbarElements);
        });
    }

    @Override
    public void reloadToolbar() {
        if (toolbarElements == null) toolbarElements = createToolbarElements();
        View.controller().toolBar.getItems().addAll(toolbarElements);
    }

    private List<Node> createToolbarElements() {
        ImageView openTerminal = Toolkit.createToolbarImage("db-blue.png");
        Tooltip removeCacheTip = new Tooltip("Open MySQL");
        Tooltip.install(openTerminal, removeCacheTip);
        openTerminal.setOnMouseClicked(MysqlTerminal::openMysqlTerminal);

        List<Node> nodes = new ArrayList<>();
        if (!SSH.getModuleByName("SSH").isInstalled(VM.getOrThrow()))
            nodes.add(new Separator(Orientation.VERTICAL));
        nodes.add(openTerminal);
        return nodes;
    }

    @Override
    public int toolbarOrder() {
        return 1001;
    }

    public SqlConnection createConnection() {
        return new SqlConnection(this, VM.getOrThrow());
    }

    private List<Node> toolbarElements;
}
