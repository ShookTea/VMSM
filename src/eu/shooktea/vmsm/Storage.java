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

import eu.shooktea.vmsm.config.JsonFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage class manages configuration file.
 */
public class Storage {
    private Storage() {}

    /**
     * Register new virtual machine to be used by VMSM and saves configuration data.
     * @param vm new virtual machine to be added to VMSM
     */
    public static void registerVM(VirtualMachine vm) {
        vmList.add(vm);
        saveAll();
    }

    /**
     * Removes virtual machine from VMSM storage and saves configuration data.
     * @param vm virtual machine to be removed from VMSM
     */
    public static void removeVM(VirtualMachine vm) {
        vmList.remove(vm);
        saveAll();
    }

    public static void saveAll() {
        try {
            trySaveAll();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void trySaveAll() throws IOException {
        new JsonFormat().save();
    }

    static void loadAll() {
        try {
            tryLoadAll();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void tryLoadAll() throws IOException {
        new JsonFormat().load();
    }

    /**
     * Returns list of ignored Vagrant machines. These machines were added to that list during scan for existing
     * VMs that weren't manually added to VMSM by user. If user doesn't want to see notifications about VM every
     * one minute, ignoring VM will add that VM to this list. During scan VMSM doesn't display notification about
     * any of the VMs in that list.
     * <p>
     * The scan itself is run by command {@code vagrant global-status}.
     * @return list of Vagrant machines ignored by user.
     */
    public static List<String> getIgnoredVagrantMachines() {
        return ignoredVagrantMachines;
    }

    /**
     * Returns list of registered virtual machines. That list should be used only to read data; while you can
     * change content of that list, it won't be saved automatically - added or removed list will be updated in configuration
     * file only after calling {@link #saveAll()}, either directly or indirectly by doing some action that uses that method.
     * <p>
     * If you want to add new virtual machine or remove existing one, use other methods of {@link Storage} class.
     * @return list of registered virtual machines
     * @see #registerVM(VirtualMachine)
     * @see #removeVM(VirtualMachine)
     */
    public static ObservableList<VirtualMachine> getVmList() {
        return vmList;
    }

    private static final ObservableList<VirtualMachine> vmList = FXCollections.observableArrayList();
    private static List<String> ignoredVagrantMachines = new ArrayList<>();

    /**
     *Map used as configuration of VMSM. Values are stored in JSON under "config" label. These values can be of any
     * correct JSON type, including {@link JSONObject} and {@link JSONArray}.
     */
    public static Map<String, Object> config = new HashMap<>();
}
