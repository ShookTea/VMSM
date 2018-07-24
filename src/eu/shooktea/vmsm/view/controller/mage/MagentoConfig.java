package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.MagentoReport;
import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class MagentoConfig implements StageController {
    @FXML private TextField magentoPath;
    @FXML private TextField adminLogin;
    @FXML private PasswordField adminPassword;
    @FXML private Label magentoInfo;
    @FXML private ChoiceBox<MagentoReport.HoldTime> holdReports;

    @FXML
    private void initialize() {
        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        Module module = Module.getModuleByName("Magento");
        loadSetting(module, vm, magentoPath, "path");
        loadSetting(module, vm, adminLogin, "adm_login");
        loadSetting(module, vm, adminPassword, "adm_pass");

        holdReports.setItems(MagentoReport.HoldTime.createList());
        Long holdValue = null;
        Object readHoldValue = module.getSetting(vm, "report_keep_time");
        if (readHoldValue instanceof Long) {
            holdValue = (Long)readHoldValue;
        }
        if (readHoldValue instanceof Integer) {
            holdValue = ((Integer)readHoldValue).longValue();
        }

        if (holdValue == null) holdReports.setValue(MagentoReport.HoldTime.MONTH);
        else {
            MagentoReport.HoldTime time = MagentoReport.HoldTime.fromTime(holdValue);
            holdReports.setValue(time == null ? MagentoReport.HoldTime.MONTH : time);
        }
    }

    private void loadSetting(Module module, VirtualMachine vm, TextField field, String name) {
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
        fileChooser.setInitialDirectory(Start.virtualMachineProperty.getValue().getMainPath());
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
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/MagentoConfig.fxml", "Magento Config", true);
    }

    @FXML
    private void saveSettings() {
        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        Module module = Module.getModuleByName("Magento");
        File file = new File(magentoPath.getText().trim());
        if (!checkFile(file)) {
            module.removeSetting(vm, "path");
        }
        else {
            saveConf(magentoPath, "path", module, vm);
        }

        saveConf(adminLogin, "adm_login", module, vm);
        saveConf(adminPassword, "adm_pass", module, vm);

        module.setSetting(vm, "report_keep_time", holdReports.getValue().timeMillis);

        Storage.saveAll();
        stage.close();
    }

    private void saveConf(TextField textField, String name, Module module, VirtualMachine vm) {
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
