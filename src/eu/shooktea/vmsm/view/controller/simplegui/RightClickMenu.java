package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.controller.NewVM;
import eu.shooktea.vmsm.view.controller.VmManager;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RightClickMenu {
    RightClickMenu() {
        menu = createContextMenu();
    }

    public ContextMenu getContextMenu() {
        return menu;
    }

    private ContextMenu createContextMenu() {
        List<MenuItem> list = new ArrayList<>();
        list.add(vmMenu());
        list.add(exit());

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(list);
        return menu;
    }

    private MenuItem separator() {
        return new SeparatorMenuItem();
    }

    private MenuItem newVm() {
        MenuItem item = new MenuItem("Create new VM...");
        item.setOnAction(NewVM::openNewVmWindow);
        return item;
    }

    private MenuItem exit() {
        MenuItem item = new MenuItem("Exit VMSM");
        item.setOnAction(e -> System.exit(0));
        return item;
    }

    private MenuItem vmMenu() {
        if (!VM.isSet()) return newVm();

        List<MenuItem> items = new ArrayList<>();
        ToggleGroup group = vmToggleGroup();
        for (VirtualMachine vm : Storage.getVmList().sorted(Comparator.comparing(VirtualMachine::getName))) {
            RadioMenuItem vmItem = new RadioMenuItem(vm.getName());
            vmItem.setToggleGroup(group);
            if (VM.isEqual(vm)) group.selectToggle(vmItem);
            items.add(vmItem);
        }
        items.add(separator());
        items.add(newVm());
        items.add(vmManager());
        Menu menu = new Menu("VMs");
        menu.getItems().addAll(items);
        return menu;
    }

    private ToggleGroup vmToggleGroup() {
        ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioMenuItem item = (RadioMenuItem)newValue;
            String name = item.getText();
            VirtualMachine chosenMachine = Storage.getVmList().stream()
                    .filter(vm -> vm.getName().equals(name))
                    .findFirst()
                    .get();
            VM.set(chosenMachine);
        });
        return group;
    }

    private MenuItem vmManager() {
        MenuItem item = new MenuItem("VM Manager...");
        item.setOnAction(VmManager::openVmManagerWindow);
        return item;
    }

    private final ContextMenu menu;
}
