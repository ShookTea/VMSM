package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.vmsm.module.VMModule;

public class DockerCompose extends VMModule {
    @Override
    public String getName() {
        return "Docker Compose";
    }

    @Override
    public String getDescription() {
        return "Set of toolkits for Docker Compose";
    }
}
