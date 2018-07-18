package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;

public class ModuleConfig {

    public static void openModuleConfigWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/ModuleConfig.fxml", "Module configuration", true);
    }
}
