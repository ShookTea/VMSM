package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.module.VMModule;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.List;

public class DockerCompose extends VMModule {
    @Override
    public String getName() {
        return "Docker Compose";
    }

    @Override
    public String getDescription() {
        return "Set of toolkits for Docker Compose";
    }

    @Override
    public List<ImageView> getQuickGuiButtons() {
        ImageView openDocker = Toolkit.createQuickGuiButton("docker_logo.png", "Open Docker Compose");
        return Collections.singletonList(openDocker);
    }
}
