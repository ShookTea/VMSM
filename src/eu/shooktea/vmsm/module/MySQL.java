package eu.shooktea.vmsm.module;

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
    public Module[] getDependencies() {
        return new Module[] {
                Module.getModuleByName("SSH")
        };
    }

    @Override
    public Optional<Runnable> openConfigWindow() {
        return Optional.of(MysqlConfig::openMysqlConfigWindow);
    }

    @Override
    public int toolbarOrder() {
        return 1001;
    }
}
