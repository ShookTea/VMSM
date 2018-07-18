package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.beans.binding.When;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;


public class ModuleConfig {
    @FXML private GridPane grid;

    @FXML
    private void initialize() {
        grid.setStyle("-fx-background-color: lightgray; -fx-vgap: 1; -fx-hgap: 1; -fx-padding: 1;");

        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        VMType type = vm.getType();
        Module[] modules = type.getModules().get();
        int index = 0;
        for (Module module : modules) {
            createModuleEntry(module, index);
            index += 2;
        }
        ColumnConstraints textColumn = new ColumnConstraints();
        textColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(textColumn);
    }

    private void createModuleEntry(Module module, int rowIndex) {
        Label name = new Label(module.getName());
        name.setFont(new Font(22));
        name.setMaxWidth(Double.MAX_VALUE);

        Label description = new Label(module.getDescription());
        description.setMaxWidth(Double.MAX_VALUE);

        ToggleButton switchButton = new ToggleButton();
        switchButton.textProperty().bind(
                new When(switchButton.selectedProperty())
                .then("On")
                .otherwise("Off"));
        switchButton.styleProperty().bind(
                new When(switchButton.selectedProperty())
                .then("-fx-background-color: green;")
                .otherwise("-fx-background-color: red;"));
        switchButton.setMaxWidth(Double.MAX_VALUE);

        Button configButton = new Button("Config");
        configButton.setMaxWidth(Double.MAX_VALUE);

        grid.addRow(rowIndex, name, switchButton);
        grid.addRow(rowIndex + 1, description, configButton);
    }

    public static void openModuleConfigWindow(Object... lambdaArgs) {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/ModuleConfig.fxml", "Module configuration", true);
    }
}
