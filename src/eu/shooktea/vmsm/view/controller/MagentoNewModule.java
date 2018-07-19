package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class MagentoNewModule {

    @FXML private TextField namespaceField;
    @FXML private TextField nameField;
    @FXML private TextField fullModuleNameField;
    @FXML private TextField versionField;
    @FXML private ChoiceBox<String> codePoolField;
    @FXML private CheckBox model;
    @FXML private CheckBox installer;
    @FXML private CheckBox helper;
    @FXML private CheckBox block;

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

    }

    public static void openMagentoNewModuleWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/MagentoNewModule.fxml", "New module", true);
    }
}
