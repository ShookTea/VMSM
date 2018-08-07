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
package eu.shooktea.vmsm.vmtype;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.view.controller.NewVM;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Representation of type of virtual machine. While every virtual machine can have multiple modules, it will always
 * have one and only one type. That type is set when creating new virtual machine in VMSM and cannot be changed.
 */
public abstract class VMType {

    VMType() {
        typeName = new SimpleStringProperty("typeName");
        creationInfo = new SimpleStringProperty("");
        toolBarElements = new SimpleListProperty<>();
    }

    /**
     * Returns name of type that will be displayed on list of types during creation of virtual machine.
     * @return name of type
     */
    public String getTypeName() {
        return typeName.getValue();
    }

    /**
     * Returns current creation error that is displayed when something wrong is happening during creation of virtual
     * machine with selected type.
     * @return creation error
     */
    public String getCreationError() {
        return creationError.getValue();
    }

    /**
     * Returns creation information to be displayed in new virtual machine window.
     * <p>
     * It IS used in {@link NewVM#initialize()}. Do not remove it.
     * @return creation info
     */
    public String getCreationInfo() {
        return creationInfo.get();
    }

    private void setCreationError(String err) {
        creationError.setValue(err);
    }

    /**
     * Checks file that has been selected as root file for virtual machine of that type. If root file is incorrect,
     * it will store information about that in creation error. Otherwise it will store empty string there.
     * @param file VM's root file selected by user
     * @see #getCreationError()
     */
    public final void checkVmRootFile(File file) {
        setCreationError(checkRootFile(file));
    }

    /**
     * Returns array with names of VMSM modules that are compatible with selected VM.
     * @return array of modules' names.
     */
    public String[] getModules() {
        return new String[0];
    }

    /**
     * Creates list of quick menu buttons. It takes all installed modules on virtual machine, sort them by their sort
     * value and returns their buttons.
     * @return list of quick menu buttons
     * @see VMModule#getSortValue()
     */
    public List<ImageView> getQuickGuiButtons() {
        return getInstalledModulesStream(VM.getOrThrow())
                .map(VMModule::getQuickGuiButtons)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns optional that can contain menu item for current virtual machine. If that menu item is a full {@link javafx.scene.control.Menu},
     * it will be used as root for module menu items.
     * @param vm current virtual machine
     * @return optional that can contain menu item.
     */
    public Optional<MenuItem> getMenuItem(VirtualMachine vm) {
        return Optional.empty();
    }

    /**
     * Returns list of modules' menu items. It takes all installed modules on virtual machine, sort them by their sort
     * value and returns their menu items.
     * @param vm current virtual machine
     * @return list of menu items
     */
    public List<MenuItem> getMenuItemsWithModules(VirtualMachine vm) {
        List<MenuItem> list = new ArrayList<>();
        list.addAll(getInstalledModulesStream(vm)
                .map(VMModule::getMenuItem)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
        return list;
    }

    private Stream<VMModule> getInstalledModulesStream(VirtualMachine vm) {
        return Arrays.stream(getModules())
                .map(VMModule::getModuleByName)
                .filter(Objects::nonNull)
                .map(obj -> (VMModule)obj)
                .filter(mod -> mod.isInstalled(vm))
                .sorted(Comparator.comparing(VMModule::getSortValue));
    }

    /**
     * Updates type data. Runs every 5 seconds.
     * @param vm current virtual machine.
     */
    public void update(VirtualMachine vm) {}

    /**
     * Check whether this file can be correct root file for VM.
     * @param file root file choosen by user.
     * @return empty string or {@code null} if file is correct, error information otherwise.
     */
    protected String checkRootFile(File file) {
        return "";
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    /**
     * Checks if root path of VM should be a directory.
     * @return
     */
    public boolean isMainPathDirectory() {
        return true;
    }

    ReadOnlyStringProperty typeName;
    ReadOnlyStringProperty creationInfo;
    private ListProperty<Node> toolBarElements;
    private final StringProperty creationError = new SimpleStringProperty("");

    /**
     * Returns type of VM based on it's name.
     * @param name name of VM type
     * @return type with selected name, or {@code null} if VM type with that name has not been found.
     */
    public static VMType getByName(String name) {
        return types.stream()
                .filter(type -> type.getTypeName().equals(name))
                .findAny().get();
    }

    /**
     * Returns observable list containing all types of virtual machines.
     */
    public static final ObservableList<VMType> types = FXCollections.observableArrayList(
            new Vagrant()
    );

}
