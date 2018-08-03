package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class VmManager {
    @FXML private TableView<VirtualMachine> table;

    @FXML
    private void initialize() {
        createColumns();
        initEvents();
        table.setItems(Storage.getVmList());
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

    private void initEvents() {
        table.setRowFactory(tv -> {
            TableRow<VirtualMachine> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && (!row.isEmpty())) {
                    VirtualMachine vm = row.getItem();
                    NewVM.openNewVmWindow(vm);
                }
                else if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1 && (!row.isEmpty())) {
                    VirtualMachine vm = row.getItem();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Deleting VM");
                    alert.setHeaderText(vm.getName());
                    alert.setContentText("Are you sure you want to delete \"" + vm.getName() + "\" VM?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        Storage.removeVM(vm);
                        if (VM.isEqual(vm))
                            VM.unset();
                    }
                }
            });
            return row;
        });
    }

    @FXML
    private void createNewVM() {
        NewVM.openNewVmWindow();
    }

    public static void openVmManagerWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/VmManager.fxml", "VM Manager");
    }
}
