package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Module;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class MagentoConfig {
    @FXML private TextField magentoPath;
    @FXML private Label magentoInfo;

    @FXML
    private void openPathWindow() {
        String dialogTitle = "Choose path";
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(dialogTitle);
        File file = fileChooser.showDialog(Start.primaryStage);
        if (file == null) return;

        File indexFile = new File(file, "index.php");
        File appDirectory = new File(file, "app/code");
        File varDirectory = new File(file, "var");
        if (indexFile.exists() && appDirectory.exists() && varDirectory.exists()) {
            magentoPath.setText(file.getAbsolutePath());
            magentoInfo.setTextFill(Color.GREEN);
        }
        else {
            magentoInfo.setTextFill(Color.RED);
        }
    }

    public static void openMagentoConfig(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/MagentoConfig.fxml", "Magento Config", true);
    }

    @FXML
    private void saveSettings() {
        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        Module module = Module.getModulesByName().get("Magento");

    }
}
