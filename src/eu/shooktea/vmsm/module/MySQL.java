package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.view.controller.mysql.MysqlConfig;

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
}
