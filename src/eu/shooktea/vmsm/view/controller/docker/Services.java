package eu.shooktea.vmsm.view.controller.docker;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.dockercompose.ComposeFile;
import eu.shooktea.vmsm.module.dockercompose.DockerCompose;
import eu.shooktea.vmsm.module.dockercompose.Service;
import eu.shooktea.vmsm.view.View;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import org.reactfx.value.Val;

import java.io.IOException;

public class Services {
    @FXML private ListView<Service> servicesListView;
    @FXML private ListView<Service> linksList;
    @FXML private ListView<Service> dependenciesList;
    @FXML private TextField serviceName;
    @FXML private TextField serviceSource;
    @FXML private ChoiceBox<Service.ServiceSource> serviceSourceType;

    private ObjectProperty<Service> currentService = new SimpleObjectProperty<>();
    private DockerCompose dockerCompose;
    private VirtualMachine vm;
    private ComposeFile composeFile;

    @FXML
    private void initialize() {
        dockerCompose = DockerCompose.getModuleByName("Docker Compose");
        vm = VM.getOrThrow();
        composeFile = new ComposeFile();

        servicesListView.setItems(composeFile.getServices());
        servicesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        linksList.setItems(composeFile.getServices());
        linksList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dependenciesList.setItems(composeFile.getServices());
        dependenciesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        currentService.bind(servicesListView.getSelectionModel().selectedItemProperty());
        serviceSourceType.setItems(Service.ServiceSource.listValues());

        serviceName.textProperty().bindBidirectional(Val.selectVar(currentService, Service::nameProperty));
        serviceSource.textProperty().bindBidirectional(Val.selectVar(currentService, Service::sourceProperty));
        serviceSourceType.valueProperty().bindBidirectional(Val.selectVar(currentService, Service::sourceTypeProperty));

        currentService.addListener((observable, oldValue, newValue) -> this.reload());
        this.reload();
    }

    private void reload() {
        servicesListView.refresh();
        linksList.refresh();
        dependenciesList.refresh();

        linksList.setCellFactory(CheckBoxListCell.forListView(param -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.setValue(currentService.get() != null && currentService.get().getLinks().contains(param));
            observable.addListener((obs, wasSelected, isSelected) -> {
                if (currentService.get() != null && currentService.get() != param) {
                    ListProperty<Service> links = currentService.get().linksProperty();
                    if (isSelected) links.add(param);
                    else links.remove(param);
                }
            });
            return observable;
        }));

        dependenciesList.setCellFactory(CheckBoxListCell.forListView(param -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.setValue(currentService.get() != null && currentService.get().getDependencies().contains(param));
            observable.addListener((obs, wasSelected, isSelected) -> {
                if (currentService.get() != null && currentService.get() != param) {
                    ListProperty<Service> dependencies = currentService.get().dependenciesProperty();
                    if (isSelected) dependencies.add(param);
                    else dependencies.remove(param);
                }
            });
            return observable;
        }));
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
                    reload();
                    servicesListView.getSelectionModel().select(service);
                }
            }
            composeFile.save();
            reload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeService() {
        if (currentService.isNull().get()) return;
        composeFile.getServices().remove(currentService.get());
        servicesListView.getSelectionModel().clearSelection();
        reload();
    }

    @FXML
    private void addNewService() {
        servicesListView.getSelectionModel().clearSelection();
    }

    public static void openDockerServicesWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/docker/Services.fxml", "Docker Compose services");
    }
}
