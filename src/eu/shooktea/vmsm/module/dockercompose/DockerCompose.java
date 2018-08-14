package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.view.controller.docker.Services;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.io.IOException;
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
        openDocker.setOnMouseClicked(Services::openDockerServicesWindow);
        return Collections.singletonList(openDocker);
    }

    @Override
    public Optional<MenuItem> getMenuItem() {
        Menu docker = new Menu("Docker Compose", Toolkit.createMenuImage("docker_logo.png"));

        MenuItem composeFile = new MenuItem("Compose file...");
        composeFile.setOnAction(Services::openDockerServicesWindow);

        Menu openBash = new Menu("Connect to bash");
        try {
            for (Service service : new ComposeFile().getServices()) {
                MenuItem item = new MenuItem(service.getName());
                item.setOnAction(e -> openBash(service));
                openBash.getItems().add(item);
            }
        } catch (IOException ignored) {}

        docker.getItems().addAll(composeFile, openBash);
        return Optional.of(docker);
    }

    private void openBash(Service service) {
        System.out.println(service.getName());
    }
}
