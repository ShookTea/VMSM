package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;

public class VmManager {

    public static void openVmManagerWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/VmManager.fxml", "VM Manager", true);
    }
}
