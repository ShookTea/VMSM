package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.json.JSONObject;

import java.util.HashMap;
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

    public void storeInJSON(JSONObject obj, VirtualMachine vm) {
        settings.getOrDefault(vm, new HashMap<>()).forEach(obj::put);
    }

    public void loadFromJSON(JSONObject obj, VirtualMachine vm) {
        if (!settings.containsKey(vm)) settings.put(vm, new HashMap<>());
        Map<String, String> values = settings.getOrDefault(vm, new HashMap<>());
        obj.keySet().forEach(key -> values.put(key, obj.getString(key)));
    }

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

    public static Module getModuleByName(String name) {
        return modules.get(name);
    }

    private static Map<String, Module> modules = Map.of(
            "Magento", new Magento()
    );

    public void setSetting(VirtualMachine vm, String key, String value) {
        if (!settings.containsKey(vm)) {
            settings.put(vm, new HashMap<>());
        }
        settings.getOrDefault(vm, new HashMap<>()).put(key, value);
    }

    public String getSetting(VirtualMachine vm, String key) {
        return settings.getOrDefault(vm, new HashMap<>()).getOrDefault(key, null);
    }

    public void removeSetting(VirtualMachine vm, String key) {
        settings.getOrDefault(vm, new HashMap<>()).remove(key);
    }

    private BooleanProperty isInstalled;
    private Map<VirtualMachine, Map<String, String>> settings = new HashMap<>();
}
