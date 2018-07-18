package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.beans.binding.When;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class ModuleConfig {
    @FXML private VBox modulesList;

    @FXML
    private void initialize() {
        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        VMType type = vm.getType();
        Module[] modules = type.getModules().get();
        for (Module module : modules) {
            createModuleEntry(module);
        }
    }

    private void createModuleEntry(Module module) {
        Label name = new Label(module.getName());
        name.setFont(new Font(22));
        Label description = new Label(module.getDescription());
        ToggleButton switchButton = new ToggleButton();
        switchButton.textProperty().bind(
                new When(switchButton.selectedProperty())
                .then("On")
                .otherwise("Off"));

        switchButton.styleProperty().bind(
                new When(switchButton.selectedProperty())
                .then("-fx-background-color: green;")
                .otherwise("-fx-background-color: red;"));

        VBox elements = new VBox(name, description, switchButton);
        modulesList.getChildren().add(elements);
    }

    public static void openModuleConfigWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/ModuleConfig.fxml", "Module configuration", true);
    }
}
