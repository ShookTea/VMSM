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
import java.util.List;
import java.util.stream.Collectors;

public class Storage {
    private Storage() {}

    public static void registerVM(VirtualMachine vm) {
        vmList.add(vm);
        saveAll();
    }

    public static void removeVM(VirtualMachine vm) {
        vmList.remove(vm);
        saveAll();
    }

    public static void saveAll() {
        File backup = new File(vmsmFile.getParentFile(), vmsmFile.getName() + ".backup");
        try {
            Files.copy(vmsmFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
                Files.copy(backup.toPath(), vmsmFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                backup.delete();
                System.err.println("Backup restored and deleted.");
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
        if (Start.virtualMachineProperty.isNotNull().get()) {
            root.put("current_vm", Start.virtualMachineProperty.getValue().getName());
        }

        PrintWriter pw = new PrintWriter(vmsmFile);
        pw.println(root.toString());
        pw.close();
    }

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
        if (config.isEmpty()) return;
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
                Start.virtualMachineProperty.setValue(filtered.get(0));
            }
            else if (vmList.size() > 0) {
                Start.virtualMachineProperty.setValue(vmList.get(0));
            }
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

    private static File vmsmFile = getVmsmFile();
    public static final ObservableList<VirtualMachine> vmList = FXCollections.observableArrayList();
}
