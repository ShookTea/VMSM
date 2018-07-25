package eu.shooktea.vmsm.module;

public class MySQL extends Module {
    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public String getDescription() {
        return "MySQL client. Requires SSH.";
    }

    @Override
    public Module[] getDependencies() {
        return new Module[] {
                SSH.getModuleByName("SSH")
        };
    }
}
