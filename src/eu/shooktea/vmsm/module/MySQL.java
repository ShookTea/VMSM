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
    public int toolbarOrder() {
        return 1001;
    }

    public SqlConnection createConnection() {
        return new SqlConnection(this, VM.getOrThrow());
    }
}
