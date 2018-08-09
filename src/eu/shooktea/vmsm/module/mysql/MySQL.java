package eu.shooktea.vmsm.module.mysql;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.view.controller.mysql.MysqlConfig;
import eu.shooktea.vmsm.view.controller.mysql.MysqlTerminal;
import eu.shooktea.vmsm.view.controller.mysql.TabScreen;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Module representing MySQL connection.
 * <p>
 * MySQL module can be configured by host address, port, username, password and name of database. Port by default
 * is 3306 and host address is {@code 127.0.0.1}, as most probably connection will be established via SSH tunnelling.
 * If no tunnelling is used, host address should be a proper address to VM's database.
 * <p>
 * SSH tunnelling can be also specified. If {@link eu.shooktea.vmsm.module.ssh.SSH} module is on and configured, then
 * configuration of tunnelling is taken automatically from there, otherwise it need to be filled in manually.
 */
public class MySQL extends VMModule {
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

    /**
     * Creates new connection for current VM with its configuration.
     * @return new MySQL connection
     */
    public SqlConnection createConnection() {
        return new SqlConnection(this, VM.getOrThrow());
    }

    @Override
    public int getSortValue() {
        return 1005;
    }

    @Override
    public List<ImageView> getQuickGuiButtons() {
        ImageView openTerminal = Toolkit.createQuickGuiButton("db-blue.png", "Open MySQL GUI");
        openTerminal.setOnMouseClicked(TabScreen::showTabScreen);
        return Collections.singletonList(openTerminal);
    }

    @Override
    public Optional<MenuItem> getMenuItem() {
        Menu root = new Menu("MySQL", Toolkit.createMenuImage("db-blue.png"));

        MenuItem openGui = new MenuItem("Open database...");
        openGui.setOnAction(TabScreen::showTabScreen);

        MenuItem openTerminal = new MenuItem("Open terminal...");
        openTerminal.setOnAction(MysqlTerminal::openMysqlTerminal);

        MenuItem config = new MenuItem("MySQL configuration...", Toolkit.createMenuImage("run.png"));
        config.setOnAction(MysqlConfig::openMysqlConfigWindow);

        root.getItems().addAll(openGui, openTerminal, config);
        return Optional.of(root);
    }
}
