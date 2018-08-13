package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.module.VMModule;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<MenuItem> getMenuItem() {
        MenuItem dockerCompose = new MenuItem("Docker Compose", Toolkit.createMenuImage("docker_logo.png"));
        return Optional.of(dockerCompose);
    }
}
