package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.module.Magento;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;


public class MagentoNewModule implements StageController {

    @FXML private TextField namespaceField;
    @FXML private TextField nameField;
    @FXML private TextField fullModuleNameField;
    @FXML private TextField versionField;
    @FXML private ChoiceBox<String> codePoolField;
    @FXML private CheckBox model;
    @FXML private CheckBox installer;
    @FXML private CheckBox helper;
    @FXML private CheckBox block;

    private Stage stage;

    @FXML
    private void initialize() {
        namespaceField.setOnKeyTyped(e -> updateTextFields(false));
        nameField.setOnKeyTyped(e -> updateTextFields(false));
        fullModuleNameField.setOnKeyTyped(e -> updateTextFields(true));
        codePoolField.setItems(FXCollections.observableArrayList("core", "community", "local"));
        codePoolField.setValue("local");
    }

    private void updateTextFields(boolean fullNameUpdated) {
        if (fullNameUpdated) {
            String fullName = fullModuleNameField.getText();
            if (fullName.contains("_")) {
                String[] parts = fullName.split("_");
                namespaceField.setText(parts[0].trim());
                if (parts.length > 1) nameField.setText(parts[1].trim());
            }
            else {
                namespaceField.setText("Mage");
                nameField.setText(fullName);
            }
        }
        else {
            String namespace = namespaceField.getText().trim();
            if (namespace.isEmpty()) namespace = "Mage";
            String modName = nameField.getText().trim();
            fullModuleNameField.setText(namespace + "_" + modName);
        }
    }

    @FXML
    private void createAction() {
        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        Magento magento = (Magento)Module.getModuleByName("Magento");
        String path = magento.getSetting(vm, "path");
        if (path == null) {
            showError("You haven't configured Magento main directory!");
            return;
        }
        File root = new File(path);
        File codeRoot = new File(root, "app/code");
        File moduleDeclarationRoot = new File(root, "app/etc/modules");

        String namespace = namespaceField.getText().trim();
        String moduleName = nameField.getText().trim();
        if (namespace.isEmpty()) namespace = "Mage";
        if (moduleName.isEmpty()) {
            showError("Module name cannot be empty!");
            return;
        }
        String fullModuleName = namespace + "_" + moduleName;
        String codePool = codePoolField.getValue();

        File moduleRoot = new File(codeRoot, codePool + "/" + namespace + "/" + moduleName);
        if (moduleRoot.exists()) {
            showError("Module " + fullModuleName + " exists in " + codePool + " code pool already.");
            return;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Module error");
        alert.setHeaderText("Module creation error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openMagentoNewModuleWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/MagentoNewModule.fxml", "New module", true);
    }
}
