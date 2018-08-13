package eu.shooktea.vmsm.view.controller.docker;

import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;

public class Services {

    @FXML
    private void initialize() {


    }
    public static void openDockerServicesWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/docker/Services.fxml", "Docker Compose services");
    }
}
