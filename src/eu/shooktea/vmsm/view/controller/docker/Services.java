package eu.shooktea.vmsm.view.controller.docker;

import com.amihaiemil.camel.Yaml;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.dockercompose.ComposeFile;
import eu.shooktea.vmsm.module.dockercompose.DockerCompose;
import eu.shooktea.vmsm.module.dockercompose.Service;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;

public class Services {
    @FXML private ListView<Service> servicesListView;

    private DockerCompose dockerCompose;
    private VirtualMachine vm;
    private ComposeFile composeFile;

    @FXML
    private void initialize() {
        try {
            dockerCompose = DockerCompose.getModuleByName("Docker Compose");
            vm = VM.getOrThrow();

            File input = ((eu.shooktea.vmsm.vmtype.DockerCompose)vm.getType()).getDockerComposeFile(vm);
            composeFile = new ComposeFile(Yaml.createYamlInput(input).readYamlMapping());

            servicesListView.setItems(composeFile.getServices());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openDockerServicesWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/docker/Services.fxml", "Docker Compose services");
    }
}
