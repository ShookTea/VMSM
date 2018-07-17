package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class VmManager {
    @FXML private TableView<VirtualMachine> table;

    @FXML
    private void initialize() {

    }

    @FXML
    private void createNewVM() {
        NewVM.openNewVmWindow();
    }

    public static void openVmManagerWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/VmManager.fxml", "VM Manager", true);
    }
}
