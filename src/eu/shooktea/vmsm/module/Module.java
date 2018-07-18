package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.json.JSONObject;

import java.util.Map;
import java.util.Optional;

public abstract class Module {
    public Module() {
        isInstalled = new SimpleBooleanProperty();
        isInstalled.bind(Bindings.createBooleanBinding(() ->
                Start.virtualMachineProperty.isNotNull().get()
                && Start.virtualMachineProperty.get().getModules().contains(this)
        ));
    }

    public abstract String getName();
    public abstract String getDescription();
    public abstract void storeInJSON(JSONObject obj);
    public abstract void loadFromJSON(JSONObject obj);

    public Optional<Runnable> openConfigWindow() {
        return Optional.empty();
    }
    public void afterModuleInstalled() {}
    public void afterModuleRemoved() {}
    public void afterModuleLoaded() {}
    public void afterModuleTurnedOff() {}

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Module) {
            return this.getClass().getName().equals(ob.getClass().getName());
        }
        return false;
    }

    public boolean isInstalled(VirtualMachine vm) {
        return vm.getModules().contains(this);
    }

    public void installOn(VirtualMachine vm) {
        if (isInstalled(vm)) {
            vm.getModules().remove(this);
            afterModuleRemoved();
        }
        else {
            vm.getModules().add(this);
            afterModuleInstalled();
        }
        Storage.saveAll();
    }

    public static Map<String, Module> getModulesByName() {
        return Map.of(
                "Magento", new Magento()
        );
    }

    private BooleanProperty isInstalled;
}
