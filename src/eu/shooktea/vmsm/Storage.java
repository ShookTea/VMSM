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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Tries to save configuration data.
     * If configuration file already exists, first thing method does is creating backup. With backup already existing,
     * method removes original configuration file and creates a new one.
     * <p>
     * This method should be called every time some change in configuration has been introduced, to save it for future load.
     */
    public static void saveAll() {
        try {
            Files.copy(vmsmFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to created backup");
            e.printStackTrace();
            System.exit(1);
        }
        try {
            trySaveAll();
        } catch (IOException e) {
            System.err.println("Failed to save; try for restoring backup");
            e.printStackTrace();
            try {
                Files.copy(backupFile.toPath(), vmsmFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.err.println("Backup restored.");
            } catch (IOException e1) {
                System.err.println("Failed restoring backup.");
                e1.printStackTrace();
            }
            System.exit(1);
        }
    }

    private static void trySaveAll() throws IOException {
        vmsmFile.delete();
        vmsmFile.createNewFile();
        JSONObject root = new JSONObject();

        List<JSONObject> list = vmList.stream()
                .map(VirtualMachine::toJSON)
                .collect(Collectors.toList());
        JSONArray vms = new JSONArray(list);
        root.put("VMs", vms);
        VM.ifNotNull(vm -> root.put("current_vm", vm.getName()));
        if (ignoredVagrantMachines != null && !ignoredVagrantMachines.isEmpty()) {
            JSONArray array = new JSONArray(ignoredVagrantMachines);
            root.put("ignored_vagrant_machines", array);
        }

        PrintWriter pw = new PrintWriter(vmsmFile);
        pw.println(root.toString());
        pw.close();
    }

    /**
     * Tries to load configuration data. If configuration file doesn't exist, method does nothing. If both configuration file
     * and backup file exist, but configuration file is empty, data from backup file is used instead.
     */
    public static void loadAll() {
        try {
            tryLoadAll();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void tryLoadAll() throws IOException {
        String config = new String(Files.readAllBytes(vmsmFile.toPath())).trim();
        String backupConfig = new String(Files.readAllBytes(backupFile.toPath())).trim();

        if (config.isEmpty() && backupConfig.isEmpty()) return;
        if (config.isEmpty()) config = backupConfig;

        JSONObject obj = new JSONObject(config);

        vmList.clear();
        if (obj.has("VMs")) {
            for (Object o : obj.getJSONArray("VMs")) {
                JSONObject json = (JSONObject) o;
                VirtualMachine vm = VirtualMachine.fromJSON(json);
                vmList.add(vm);
            }
        }
        if (obj.has("current_vm")) {
            String currentVmName = obj.getString("current_vm");
            List<VirtualMachine> filtered = Storage.vmList.filtered(vm -> vm.getName().equals(currentVmName));
            if (filtered.size() == 1) {
                VM.set(filtered.get(0));
            }
            else if (vmList.size() > 0) {
                VM.set(vmList.get(0));
            }
        }

        ignoredVagrantMachines = new ArrayList<>();
        if (obj.has("ignored_vagrant_machines")) {
            JSONArray array = obj.getJSONArray("ignored_vagrant_machines");
            array.iterator().forEachRemaining(entry -> ignoredVagrantMachines.add(entry.toString()));
        }
    }

    private static File getVmsmFile() {
        String homePath = System.getProperty("user.home");
        File home = new File(homePath);
        File file = new File(home, ".vmsm" + File.separator + "config.json");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return file;
    }

    private static File getBackupFile(File originalFile) {
        File file = new File(originalFile.getParentFile(), originalFile.getName() + ".backup");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return file;
    }

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

    private static File vmsmFile = getVmsmFile();
    private static File backupFile = getBackupFile(vmsmFile);
    private static final ObservableList<VirtualMachine> vmList = FXCollections.observableArrayList();
    private static List<String> ignoredVagrantMachines = new ArrayList<>();

    public static void checkVmsmFiles() {
        try {
            if (!vmsmFile.getParentFile().exists()) vmsmFile.getParentFile().mkdirs();
            if (!vmsmFile.exists())
                Files.write(vmsmFile.toPath(), "{}".getBytes(), StandardOpenOption.CREATE);
            if (!backupFile.exists())
                Files.write(backupFile.toPath(), "{}".getBytes(), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
