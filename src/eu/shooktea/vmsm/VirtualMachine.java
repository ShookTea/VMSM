/*
MIT License

Copyright (c) 2018 Norbert Kowalik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package eu.shooktea.vmsm;

import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.view.controller.ModuleConfig;
import eu.shooktea.vmsm.view.controller.simplegui.QuickGuiMenu;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Single virtual machine with HTTP server on it.
 */
public class VirtualMachine {

    /**
     * Main constructor for virtual machine.
     * @param name displayed name of virtual machine. Should be unique but doesn't need to be.
     * @param mainPath main path to virtual machine. More information about correct virtual machines are stored in {@link VMType}.
     * @param pageRoot page root of VM that is used by modules. It can be either domain or IP address.
     * @param type type of virtual machine.
     */
    public VirtualMachine(String name, File mainPath, URL pageRoot, VMType type) {
        this.name = new SimpleStringProperty(name);
        this.mainPath = new SimpleObjectProperty<>(mainPath);
        this.pageRoot = new SimpleObjectProperty<>(pageRoot);
        this.type = new SimpleObjectProperty<>(type);
        this.modules = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    /**
     * Converts virtual machine to JSON object. That JSON object is then stored in configuration file. During loading,
     * virtual machine should be fully recoverable from that JSON.
     * @return JSON representation of virtual machine
     * @see #fromJSON(JSONObject)
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name", name.get());
        obj.put("path", mainPath.get().getAbsolutePath());
        if (pageRoot.isNotNull().get()) obj.put("url", pageRoot.get().toString());
        obj.put("type", type.get().getTypeName());

        JSONObject modules = new JSONObject();
        for (VMModule module : getModules()) {
            JSONObject config = new JSONObject();
            module.storeInJSON(config, this);
            modules.put(module.getName(), config);
        }
        obj.put("modules", modules);
        return obj;
    }

    /**
     * Returns display name of virtual machine.
     * @return name of virtual machine
     */
    public String getName() {
        return name.getValue();
    }

    /**
     * Returns main path to virtual machine.
     * @return main path to virtual machine
     */
    public File getMainPath() {
        return mainPath.getValue();
    }

    /**
     * Sets new main path to virtual machine.
     * @param f new main path
     */
    public void setMainPath(File f) {
        mainPath.setValue(f);
    }

    /**
     * Returns page root of virtual machine. Can be {@code null}.
     * @return page root
     */
    public URL getPageRoot() {
        return pageRoot.get();
    }

    /**
     * Sets new page root of virtual machine. Can be {@code null}.
     * @param url new page root
     */
    public void setPageRoot(URL url) {
        pageRoot.set(url);
    }

    /**
     * Returns page root property.
     * @return page root property
     */
    public ObjectProperty<URL> pageRootProperty() {
        return pageRoot;
    }

    /**
     * Returns type of virtual machine.
     * @return type of VM
     */
    public VMType getType() {
        return type.getValue();
    }

    /**
     * Returns list of all modules installed currently on that virtual machine.
     * @return list of installed modules
     */
    public ObservableList<VMModule> getModules() {
        return modules.getValue();
    }

    /**
     * Runs update of virtual machine. Update of VM consists of two parts: updating VM's type and all VM's modules.
     * This method can be runned every time, but it's also called for currently displayed VM in 5-second loop.
     */
    public void update() {
        getType().update(this);
        modules.forEach(VMModule::loopUpdate);
    }

    /**
     * Returns VM status property.
     * @return status property
     * @see Status
     */
    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    /**
     * Sets new status of VM.
     * @param status new status
     * @see Status
     */
    public void setStatus(Status status) {
        statusProperty().setValue(status);
    }

    /**
     * Returns status of VM.
     * @return status of VM
     * @see Status
     */
    public Status getStatus() {
        return statusProperty().getValue();
    }

    /**
     * Returns VM's quick menu items. Quick menu is displayed in quarter circle after pressing left mouse button.
     * Menu items are mostly dependent from type of VM (and, indirectly, from its modules), with one exception: every
     * VM with set root address will have "Home" button that launches web browser with that address.
     * @return quick menu items of VM.
     */
    public List<ImageView> getQuickMenuItems() {
        List<ImageView> ret = new ArrayList<>();

        if (pageRoot.isNotNull().get())
            ret.add(QuickGuiMenu.toWebPage(getPageRoot(), "home.png", "Go to home page"));

        ret.addAll(getType().getQuickGuiButtons());
        return ret;
    }

    /**
     * Returns VM's main menu item. Main menu is displayed after pressing right mouse button. These are mostly dependent
     * from type of VM. If choosen VM type returns full menu instead of single menu item AND it supports at least one
     * module, this method will return that menu with additional separator and "Module configuration" menu item.
     * @return menu items of VM
     */
    public List<MenuItem> createMenuItem() {
        List<MenuItem> ret = new ArrayList<>();
        getType().getMenuItem(this).ifPresent(item -> {
            if (getType().getModules().isPresent() && item instanceof Menu) {
                MenuItem openModuleConfig = new MenuItem("Module configuration...");
                openModuleConfig.setOnAction(ModuleConfig::openModuleConfigWindow);

                Menu m = (Menu)item;
                m.getItems().addAll(new SeparatorMenuItem(), openModuleConfig);
            }
            ret.add(item);
        });
        ret.addAll(getType().getMenuItemsWithModules(this));
        return ret;
    }

    private ReadOnlyStringProperty name;
    private ObjectProperty<File> mainPath;
    private ObjectProperty<URL> pageRoot;
    private ObjectProperty<VMType> type;
    private ListProperty<VMModule> modules;
    private ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.UNDEFINED);

    /**
     * Loads virtual machine from its JSON representation.
     * @param json JSON representation of virtual machine
     * @return virtual machine loaded from JSON
     * @throws MalformedURLException if JSON contains URL of virtual machine, but that URL is invalid
     * @see #toJSON()
     */
    public static VirtualMachine fromJSON(JSONObject json) throws MalformedURLException {
        String name = json.getString("name");
        File path = new File(json.getString("path"));
        URL url = json.has("url") ? new URL(json.getString("url")) : null;
        VMType type = VMType.getByName(json.getString("type"));
        VirtualMachine vm = new VirtualMachine(name, path, url, type);

        JSONObject modules = json.has("modules") ? json.getJSONObject("modules") : new JSONObject();
        for (String moduleName : modules.keySet()) {
            VMModule module = VMModule.getModuleByName(moduleName);
            module.loadFromJSON(modules.getJSONObject(module.getName()), vm);
            vm.getModules().add(module);
        }
        return vm;
    }
}
