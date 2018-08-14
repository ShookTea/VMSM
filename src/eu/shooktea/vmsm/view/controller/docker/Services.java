package eu.shooktea.vmsm.view.controller.docker;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.dockercompose.ComposeFile;
import eu.shooktea.vmsm.module.dockercompose.DockerCompose;
import eu.shooktea.vmsm.module.dockercompose.Service;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.yaml.YamlMap;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.reactfx.value.Val;

import java.io.IOException;

public class Services {
    @FXML private ListView<Service> servicesListView;
    @FXML private TextField serviceName;
    @FXML private TextField serviceSource;
    @FXML private ChoiceBox<Service.ServiceSource> serviceSourceType;

    private ObjectProperty<Service> currentService = new SimpleObjectProperty<>();
    private DockerCompose dockerCompose;
    private VirtualMachine vm;
    private ComposeFile composeFile;

    @FXML
    private void initialize() {
        try {
            dockerCompose = DockerCompose.getModuleByName("Docker Compose");
            vm = VM.getOrThrow();
            composeFile = new ComposeFile();

            servicesListView.setItems(composeFile.getServices());
            servicesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            currentService.bind(servicesListView.getSelectionModel().selectedItemProperty());
            serviceSourceType.setItems(Service.ServiceSource.listValues());

            serviceName.textProperty().bindBidirectional(Val.selectVar(currentService, Service::nameProperty));
            serviceSource.textProperty().bindBidirectional(Val.selectVar(currentService, Service::sourceProperty));
            serviceSourceType.valueProperty().bindBidirectional(Val.selectVar(currentService, Service::sourceTypeProperty));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveServiceData() {
        try {
            if (currentService.isNull().get() && serviceName.getText() != null && serviceSource.getText() != null && serviceSourceType.getValue() != null) {
                String name = serviceName.getText().trim();
                String source = serviceSource.getText().trim();
                Service.ServiceSource sourceType = serviceSourceType.getValue();
                if (!name.isEmpty() && !source.isEmpty()) {
                    Service service = new Service(name, composeFile);
                    service.setSource(source);
                    service.setSourceType(sourceType);
                    composeFile.getServices().addAll(service);
                    servicesListView.refresh();
                    servicesListView.getSelectionModel().select(service);
                }
            }
            composeFile.save();
            servicesListView.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeService() {
        if (currentService.isNull().get()) return;
        composeFile.getServices().remove(currentService.get());
        servicesListView.getSelectionModel().clearSelection();
        servicesListView.refresh();
    }

    @FXML
    private void addNewService() {
        servicesListView.getSelectionModel().clearSelection();
    }

    public static void openDockerServicesWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/docker/Services.fxml", "Docker Compose services");
    }
}
