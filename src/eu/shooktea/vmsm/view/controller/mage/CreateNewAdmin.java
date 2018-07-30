package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.MySQL;
import javafx.scene.control.Alert;


public class CreateNewAdmin {

    public static void openAdminCreationWindow(Object... lambdaArgs) {
        MySQL sql = MySQL.getModuleByName("MySQL");
        VirtualMachine vm = VM.getOrThrow();
        if (!sql.isInstalled(vm)) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setContentText("You need to configure MySQL module first.");
            dialog.showAndWait();
        }
        else {

        }
    }
}
