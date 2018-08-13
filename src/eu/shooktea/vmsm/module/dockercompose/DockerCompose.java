package eu.shooktea.vmsm.module.dockercompose;

import com.amihaiemil.camel.Yaml;
import com.amihaiemil.camel.YamlInput;
import com.amihaiemil.camel.YamlMapping;
import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.event.Event;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.io.File;
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
        openDocker.setOnMouseClicked(this::openDockerCompose);
        return Collections.singletonList(openDocker);
    }

    @Override
    public Optional<MenuItem> getMenuItem() {
        MenuItem dockerCompose = new MenuItem("Docker Compose", Toolkit.createMenuImage("docker_logo.png"));
        dockerCompose.setOnAction(this::openDockerCompose);
        return Optional.of(dockerCompose);
    }

    private void openDockerCompose(Event e) {
        try {
            VirtualMachine vm = VM.getOrThrow();
            VMType type = vm.getType();
            eu.shooktea.vmsm.vmtype.DockerCompose dockerType = (eu.shooktea.vmsm.vmtype.DockerCompose)type;
            File file = dockerType.getDockerComposeFile(vm);
            YamlMapping mapping = Yaml.createYamlInput(file).readYamlMapping();
            ComposeFile compose = new ComposeFile(mapping);
            compose.test();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
