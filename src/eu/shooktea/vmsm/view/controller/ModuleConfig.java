package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.view.View;
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

import java.util.*;


public class ModuleConfig {
    @FXML private GridPane grid;

    @FXML
    private void initialize() {
        grid.setStyle("-fx-background-color: lightgray; -fx-vgap: 1; -fx-hgap: 1; -fx-padding: 1;");

        VirtualMachine vm = VM.getOrThrow();
        VMType type = vm.getType();
        String[] modules = type.getModules().get();
        int index = 0;
        for (String module : modules) {
            createModuleEntry(vm, Module.getModuleByName(module), index);
            index += 2;
        }
        ColumnConstraints textColumn = new ColumnConstraints();
        textColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(textColumn);
    }

    private void createModuleEntry(VirtualMachine vm, Module module, int rowIndex) {
        Label name = new Label(module.getName());
        name.setFont(new Font(22));
        name.setMaxWidth(Double.MAX_VALUE);

        Label description = new Label(module.getDescription());
        description.setMaxWidth(Double.MAX_VALUE);

        Button configButton = new Button("Config");
        configButton.setMaxWidth(Double.MAX_VALUE);
        configButton.setDisable(!module.isInstalled(vm) || !module.openConfigWindow().isPresent());
        module.openConfigWindow().ifPresent(r -> configButton.setOnAction(e -> r.run()));

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
        switchButton.setSelected(module.isInstalled(vm));
        switchButton.setOnAction((e) -> {
            module.installOn(vm);
            configButton.setDisable(!module.isInstalled(vm) || !module.openConfigWindow().isPresent());
            updateSwitchButtons();
        });
        switchButtons.put(switchButton, module);
        updateSwitchButtons();

        grid.addRow(rowIndex, name, switchButton);
        grid.addRow(rowIndex + 1, description, configButton);
    }

    private void updateSwitchButtons() {
        VirtualMachine vm = VM.getOrThrow();
        switchButtons.forEach((button, module) -> {
            button.setDisable(Arrays.stream(module.getDependencies()).anyMatch(m -> !m.isInstalled(vm)));
            if (button.isDisable() && button.isSelected()) {
                button.setSelected(false);
                button.getOnAction().handle(null);
            }
        });
    }

    private Map<ToggleButton,Module> switchButtons = new HashMap<>();

    public static void openModuleConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/ModuleConfig.fxml", "Module configuration");
    }
}
