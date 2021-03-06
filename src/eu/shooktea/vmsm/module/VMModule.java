package eu.shooktea.vmsm.module;

import eu.shooktea.datamodel.DataModelMap;
import eu.shooktea.datamodel.DataModelValue;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.dockercompose.DockerCompose;
import eu.shooktea.vmsm.module.mage.Magento;
import eu.shooktea.vmsm.module.mysql.MySQL;
import eu.shooktea.vmsm.module.ssh.SSH;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.util.*;

/**
 * Abstract class representing single module. Module in VMSM is an additional set of tools that can be connected
 * with virtual machine. Every virtual machine can have different set of modules with different configuration.
 * {@link eu.shooktea.vmsm.vmtype.VMType} describes which modules can work with given type of virtual machine.
 * <p>
 * Because of the way modules work in VMSM, it's constructor should never be called anywhere except in declaration
 * of modules in {@code VMModule} class, where new modules are created. Modules there should be effectively singletons.
 */
public abstract class VMModule {
    /**
     * Main constructor of VMModule. Every module that rewrites it's constructor should call also this constructor. It is
     * only called once, during initialization of modules.
     */
    protected VMModule() {
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
    public VMModule[] getDependencies() {
        return new VMModule[0];
    }

    /**
     * Stores configuration of module for VM in map.
     * @param obj map that will hold configuration of module
     * @param vm virtual machine that contains configuration of module
     */
    public void storeInMap(DataModelMap obj, VirtualMachine vm) {
        settings.getOrDefault(vm, new DataModelMap()).forEach(obj::put);
    }

    /**
     * Loads configuration of module for VM from map.
     * @param obj map that holds configuration of module
     * @param vm virtual machine that will contain configuration of module
     */
    public void loadFromMap(DataModelMap obj, VirtualMachine vm) {
        if (!settings.containsKey(vm)) settings.put(vm, new DataModelMap());
        DataModelMap values = settings.getOrDefault(vm, new DataModelMap());
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
        if (ob instanceof VMModule) {
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
    public static <T extends VMModule> T getModuleByName(String name) {
        switch (name.toUpperCase()) {
            case "MAGENTO": return (T)magento;
            case "SSH": return (T)ssh;
            case "MYSQL": return (T)mysql;
            case "DOCKER COMPOSE": return (T)dockerCompose;
            default: return null;
        }
    }

    private static final MySQL mysql = new MySQL();
    private static final Magento magento = new Magento();
    private static final SSH ssh = new SSH();
    private static final DockerCompose dockerCompose = new DockerCompose();

    /**
     * Returns list of buttons to be displayed in quick menu.
     * @return list of buttons
     */
    public List<ImageView> getQuickGuiButtons() {
        return new ArrayList<>();
    }

    /**
     * Returns menu item to right click menu. It can be any class extending {@link MenuItem}, including {@link javafx.scene.control.Menu}.
     * If {@link Optional#empty()} is returned, nothing will be displayed.
     * @return optional containing menu item.
     */
    public Optional<MenuItem> getMenuItem() {
        return Optional.empty();
    }

    /**
     * Sets object to be stored in configuration of virtual machine. It can be any correct YAML value.
     * @param vm virtual machine that contains configuration
     * @param key name of setting
     * @param value value of setting
     */
    public void setSetting(VirtualMachine vm, String key, Object value) {
        if (!settings.containsKey(vm))
            settings.put(vm, new DataModelMap());
        settings.getOrDefault(vm, new DataModelMap()).put(key, value);
    }

    /**
     * Returns string from configuration.
     * @param vm virtual machine that contains configuration
     * @param key name of setting
     * @return value of setting or {@code null} if virtual machine doesn't contain setting with given name
     * @see #getSetting(VirtualMachine, String)
     */
    public String getStringSetting(VirtualMachine vm, String key) {
        DataModelValue val = getSetting(vm, key);
        if (val == null || !val.isPrimitive()) return null;
        else return val.<String>toPrimitive().getContent();
    }

    /**
     * Returns object from configuration.
     * @param vm virtual machine that contains configuration
     * @param key name of setting
     * @param defaultValue default value
     * @return DataModelValue of setting or default value if virtual machine doesn't contain setting with given name
     */
    public DataModelValue getSetting(VirtualMachine vm, String key, DataModelValue defaultValue) {
        return settings.getOrDefault(vm, new DataModelMap()).getOrDefault(key, defaultValue);
    }

    /**
     * Returns object from configuration.
     * @param vm virtual machine that contains configuration
     * @param key name of setting
     * @return DataModelValue of setting or {@code null} if virtual machine doesn't contain setting with given name
     */
    public DataModelValue getSetting(VirtualMachine vm, String key) {
        return getSetting(vm, key, null);
    }

    /**
     * Removes object from configuration
     * @param vm virtual machine that contains configuration
     * @param key name of setting that should be removed
     */
    public void removeSetting(VirtualMachine vm, String key) {
        settings.getOrDefault(vm, new DataModelMap()).remove(key);
    }

    /**
     * Returns sort value of module that is used to display quick menu buttons and main menu items in proper order. Modules
     * are sorted by that value in ascending order.
     * @return sort value.
     */
    public int getSortValue() {
        return 0;
    }

    private BooleanProperty isInstalled;
    private Map<VirtualMachine, Map<String, Object>> oldSettings = new HashMap<>();
    private Map<VirtualMachine, DataModelMap> settings = new HashMap<>();
}
