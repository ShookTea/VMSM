package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.net.URL;

public class VmManager {
    @FXML private TableView<VirtualMachine> table;

    @FXML
    private void initialize() {
        createColumns();
        table.setItems(Storage.vmList);
    }

    private void createColumns() {
        TableColumn<VirtualMachine, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<VirtualMachine, VMType> type = new TableColumn<>("Type");
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<VirtualMachine, File> path = new TableColumn<>("Root directory");
        path.setCellValueFactory(new PropertyValueFactory<>("mainPath"));

        TableColumn<VirtualMachine, URL> url = new TableColumn<>("URL");
        url.setCellValueFactory(new PropertyValueFactory<>("pageRoot"));

        table.getColumns().addAll(name, type, path, url);
    }

    @FXML
    private void createNewVM() {
        NewVM.openNewVmWindow();
    }

    public static void openVmManagerWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/VmManager.fxml", "VM Manager", true);
    }
}
