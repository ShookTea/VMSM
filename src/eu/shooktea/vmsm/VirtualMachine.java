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

import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.vmtype.VMType;
import eu.shooktea.vmsm.vmtype.Vagrant;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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
        for (Module module : getModules()) {
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
    public ObservableList<Module> getModules() {
        return modules.getValue();
    }

    /**
     * Runs update of virtual machine. Update of VM consists of two parts: updating VM's type and all VM's modules.
     * This method can be runned every time, but it's also called for currently displayed VM in 5-second loop.
     */
    public void update() {
        getType().update(this);
        modules.forEach(Module::loopUpdate);
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public void setStatus(Status status) {
        statusProperty().setValue(status);
    }

    public Status getStatus() {
        return statusProperty().getValue();
    }

    private ReadOnlyStringProperty name;
    private ObjectProperty<File> mainPath;
    private ObjectProperty<URL> pageRoot;
    private ObjectProperty<VMType> type;
    private ListProperty<Module> modules;
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
            Module module = Module.getModuleByName(moduleName);
            module.loadFromJSON(modules.getJSONObject(module.getName()), vm);
            vm.getModules().add(module);
        }
        return vm;
    }

    public enum Status {
        RUNNING, STOPPED, UNDEFINED;

        public String getResourceName() {
            switch (this) {
                case RUNNING:   return "green_ball.png";
                case STOPPED:   return "red_ball.png";
                case UNDEFINED: return "yellow_ball.png";
                default: throw new RuntimeException();
            }
        }

        public String getTooltipText() {
            switch (this) {
                case RUNNING:   return "VM is on.";
                case STOPPED:   return "VM is off.";
                case UNDEFINED: return null;
                default: throw new RuntimeException();
            }
        }
    }
}
