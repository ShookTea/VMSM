package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Module;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class MagentoConfig implements StageController {
    @FXML private TextField magentoPath;
    @FXML private Label magentoInfo;

    @FXML
    private void initialize() {
        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        Module module = Module.getModuleByName("Magento");
        String path = module.getSetting(vm, "path");
        if (path != null) {
            magentoPath.setText(path);
        }
    }

    @FXML
    private void openPathWindow() {
        String dialogTitle = "Choose path";
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(dialogTitle);
        File file = fileChooser.showDialog(Start.primaryStage);
        if (file == null) return;

        if (checkFile(file)) {
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
        Module module = Module.getModuleByName("Magento");
        String trim = magentoPath.getText().trim();
        if (trim.isEmpty()) {
            module.removeSetting(vm, "path");
        }
        File file = new File(trim);
        if (!checkFile(file)) {
            module.removeSetting(vm, "path");
        }
        module.setSetting(vm, "path", file.toString());

        Storage.saveAll();
        stage.close();
    }

    private boolean checkFile(File file) {
        File indexFile = new File(file, "index.php");
        File appDirectory = new File(file, "app/code");
        File varDirectory = new File(file, "var");
        return indexFile.exists() && appDirectory.exists() && varDirectory.exists();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;
}
