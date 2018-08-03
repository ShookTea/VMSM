package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract class representing single module. Module in VMSM is an additional set of tools that can be connected
 * with virtual machine. Every virtual machine can have different set of modules with different configuration.
 * {@link eu.shooktea.vmsm.vmtype.VMType} describes which modules can work with given type of virtual machine.
 * <p>
 * Because of the way modules work in VMSM, it's constructor should never be called anywhere except in declaration
 * of modules in {@code Module} class, where new modules are created. Modules there should be effectively singletons.
 */
public abstract class Module {
    /**
     * Main constructor of Module. Every module that rewrites it's constructor should call also this constructor. It is
     * only called once, during initialization of modules.
     */
    protected Module() {
        isInstalled = new SimpleBooleanProperty();
        isInstalled.bind(Bindings.createBooleanBinding(() ->
                VM.isSet() && VM.getOrThrow().getModules().contains(this)
        ));
    }

    /**
     * Returns name of module to be displayed in list of modules. Name should be short and (for security reasons) it
     * shouldn't use internationalization.
     * @return name of module
     */
    public abstract String getName();

    /**
     * Longer description of module to be displayed in list of modules under module's name.
     * @return description of module
     */
    public abstract String getDescription();

    /**
     * Returns list of modules that need to be on in order to allow that module to be turned on.
     * @return array of dependencies
     */
    public Module[] getDependencies() {
        return new Module[0];
    }

    /**
     * Stores configuration of module for VM in JSON.
     * @param obj JSON object that will hold configuration of module
     * @param vm virtual machine that contains configuration of module
     */
    public void storeInJSON(JSONObject obj, VirtualMachine vm) {
        settings.getOrDefault(vm, new HashMap<>()).forEach(obj::put);
    }

    /**
     * Loads configuration of module for VM from JSON.
     * @param obj JSON object that holds configuration of module
     * @param vm virtual machine that will contain configuration of module
     */
    public void loadFromJSON(JSONObject obj, VirtualMachine vm) {
        if (!settings.containsKey(vm)) settings.put(vm, new HashMap<>());
        Map<String, Object> values = settings.getOrDefault(vm, new HashMap<>());
        obj.keySet().forEach(key -> values.put(key, obj.get(key)));
    }

    /**
     * Action that should be done when pressing config button in list of modules. If equal to {@link Optional#empty()},
     * configuration button will always be disabled; otherwise configuration button will be enabled when module is on.
     * @return action to be run when pressing config button.
     */
    public Optional<Runnable> openConfigWindow() {
        return Optional.empty();
    }
    /** Method that will be run after choosing VM with module toggled on */
    public void afterModuleLoaded() {}
    /** Method that will be run after choosing other machine than the one with module toggled on */
    public void afterModuleTurnedOff() {}
    /** Update method that wil be run every 5 seconds */
    public void loopUpdate() {}

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Module) {
            return this.getClass().getName().equals(ob.getClass().getName());
        }
        return false;
    }

    /**
     * Checks if module is installed on choosen VM.
     * @param vm virtual machine to be tested
     * @return {@code true} if VM has installed module, {@code false} otherwise
     * @see #installOn(VirtualMachine)
     */
    public boolean isInstalled(VirtualMachine vm) {
        if (vm == null) return false;
        return vm.getModules().contains(this);
    }

    /**
     * Switches installation of virtual machine. If virtual machine has installed module, it will be removed, otherwise
     * it will be installed.
     * @param vm virtual machine to be installed or uninstalled.
     * @see #isInstalled(VirtualMachine)
     */
    public void installOn(VirtualMachine vm) {
        if (isInstalled(vm)) {
            vm.getModules().remove(this);
        }
        else {
            vm.getModules().add(this);
        }
        Storage.saveAll();
    }

    /**
     * Returns module singleton object by given name.
     * @param name name of module
     * @param <T> class of module
     * @return module with given class
     */
    public static <T extends Module> T getModuleByName(String name) {
        switch (name.toUpperCase()) {
            case "MAGENTO": return (T)magento;
            case "SSH": return (T)ssh;
            case "MYSQL": return (T)mysql;
            default: return null;
        }
    }

    private static final MySQL mysql = new MySQL();
    private static final Magento magento = new Magento();
    private static final SSH ssh = new SSH();

    public Optional<List<ImageView>> getQuickGuiButtons() {
        return Optional.empty();
    }

    /**
     * Sets object to be stored in configuration of virtual machine. It can be any correct JSON value.
     * @param vm virtual machine that contains configuration
     * @param key name of setting
     * @param value value of setting
     */
    public void setSetting(VirtualMachine vm, String key, Object value) {
        if (!settings.containsKey(vm)) {
            settings.put(vm, new HashMap<>());
        }
        settings.getOrDefault(vm, new HashMap<>()).put(key, value);
    }

    /**
     * Returns string from configuration.
     * @param vm virtual machine that contains configuration
     * @param key name of setting
     * @return value of setting or {@code null} if virtual machine doesn't contain setting with given name
     * @see #getSetting(VirtualMachine, String)
     */
    public String getStringSetting(VirtualMachine vm, String key) {
        Object ob = getSetting(vm, key);
        if (ob == null) return null;
        else return ob.toString();
    }

    /**
     * Returns object from configuration.
     * @param vm virtual machine that contains configuration
     * @param key name of setting
     * @return value/JSON object of setting or {@code null} if virtual machine doesn't contain setting with given name
     */
    public Object getSetting(VirtualMachine vm, String key) {
        return settings.getOrDefault(vm, new HashMap<>()).getOrDefault(key, null);
    }

    /**
     * Removes object from configuration
     * @param vm virtual machine that contains configuration
     * @param key name of setting that should be removed
     */
    public void removeSetting(VirtualMachine vm, String key) {
        settings.getOrDefault(vm, new HashMap<>()).remove(key);
    }

    private BooleanProperty isInstalled;
    private Map<VirtualMachine, Map<String, Object>> settings = new HashMap<>();
}
