package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.module.mage.Magento;
import eu.shooktea.vmsm.view.StageController;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class MagentoConfig implements StageController {
    @FXML private TextField magentoPath;
    @FXML private TextField adminLogin;
    @FXML private Label magentoInfo;

    private VirtualMachine vm;
    private Magento magento;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        magento = VMModule.getModuleByName("Magento");
        loadSetting(magento, vm, magentoPath, "path");
        loadSetting(magento, vm, adminLogin, "adm_login");
    }

    private void loadSetting(VMModule module, VirtualMachine vm, TextField field, String name) {
        String value = module.getStringSetting(vm, name);
        if (value != null) {
            field.setText(value);
        }
    }

    @FXML
    private void openPathWindow() {
        String dialogTitle = "Choose path";
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(dialogTitle);
        fileChooser.setInitialDirectory(VM.getOrThrow().getMainPath());
        File file = fileChooser.showDialog(View.stage());
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
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/MagentoConfig.fxml", "Magento Config");
    }

    @FXML
    private void saveSettings() {
        File file = new File(magentoPath.getText().trim());
        if (!checkFile(file)) {
            magento.removeSetting(vm, "path");
        }
        else {
            saveConf(magentoPath, "path", magento, vm);
        }

        saveConf(adminLogin, "adm_login", magento, vm);

        Storage.saveAll();
        stage.close();
    }

    private void saveConf(TextField textField, String name, VMModule module, VirtualMachine vm) {
        String trim = textField.getText().trim();
        if (trim.isEmpty()) {
            module.removeSetting(vm, name);
        }
        else {
            module.setSetting(vm, name, trim);
        }
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
