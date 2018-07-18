package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;

public class MagentoConfig {

    public static void openMagentoConfig(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/MagentoConfig.fxml", "Magento Config", true);
    }
}
