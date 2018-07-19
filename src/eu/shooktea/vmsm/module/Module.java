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
        Map<String, Object> values = settings.getOrDefault(vm, new HashMap<>());
        obj.keySet().forEach(key -> values.put(key, obj.get(key)));
    }

    public Optional<Runnable> openConfigWindow() {
        return Optional.empty();
    }
    /** run after toggling module on */
    public void afterModuleInstalled() {}
    /** run after toggling module off */
    public void afterModuleRemoved() {}
    /** run after choosing VM with module toggled on */
    public void afterModuleLoaded() {}
    /** run after choosing other machine than the one with module toggled on */
    public void afterModuleTurnedOff() {}
    /** regular action after clearing toolbar */
    public void reloadToolbar() {}
    /** update run every 5 seconds */
    public void loopUpdate() {}

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

    public void setSetting(VirtualMachine vm, String key, Object value) {
        if (!settings.containsKey(vm)) {
            settings.put(vm, new HashMap<>());
        }
        settings.getOrDefault(vm, new HashMap<>()).put(key, value);
    }

    public String getStringSetting(VirtualMachine vm, String key) {
        Object ob = getSetting(vm, key);
        if (ob == null) return null;
        else return ob.toString();
    }

    public Object getSetting(VirtualMachine vm, String key) {
        return settings.getOrDefault(vm, new HashMap<>()).getOrDefault(key, null);
    }

    public void removeSetting(VirtualMachine vm, String key) {
        settings.getOrDefault(vm, new HashMap<>()).remove(key);
    }

    private BooleanProperty isInstalled;
    private Map<VirtualMachine, Map<String, Object>> settings = new HashMap<>();
}
