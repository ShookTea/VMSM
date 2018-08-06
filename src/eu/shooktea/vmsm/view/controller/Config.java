package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.view.View;
import javafx.stage.Stage;

public class Config implements StageController {
    private Stage stage;


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/Config.fxml", "Settings");
    }
}
