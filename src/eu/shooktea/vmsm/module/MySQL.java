package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.view.controller.mysql.MysqlConfig;
import eu.shooktea.vmsm.view.controller.mysql.MysqlTerminal;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.util.Arrays;
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

    public SqlConnection createConnection() {
        return new SqlConnection(this, VM.getOrThrow());
    }

    @Override
    public int getSortValue() {
        return 1005;
    }

    @Override
    public Optional<List<ImageView>> getQuickGuiButtons() {
        ImageView openTerminal = Toolkit.createQuickGuiButton("db-blue.png", "Open MySQL terminal");
        openTerminal.setOnMouseClicked(MysqlTerminal::openMysqlTerminal);
        return Optional.of(Arrays.asList(openTerminal));
    }

    @Override
    public Optional<MenuItem> getMenuItem() {
        Menu root = new Menu("MySQL", Toolkit.createMenuImage("db-blue.png"));

        MenuItem openTerminal = new MenuItem("Open terminal...");
        openTerminal.setOnAction(MysqlTerminal::openMysqlTerminal);

        MenuItem config = new MenuItem("MySQL configuration...", Toolkit.createMenuImage("run.png"));
        config.setOnAction(MysqlConfig::openMysqlConfigWindow);

        root.getItems().addAll(openTerminal, config);
        return Optional.of(root);
    }
}
