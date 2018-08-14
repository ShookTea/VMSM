package eu.shooktea.vmsm.config;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class JsonFormat extends AbstractFormat {
    @Override
    public void load(File file) throws IOException {
        if (!file.exists() || !file.isFile()) return;
        String config = new String(Files.readAllBytes(file.toPath())).trim();
        if (config.isEmpty()) return;

        JSONObject object = new JSONObject(config);
        loadVMs(object);
        loadCurrentVM(object);
        loadIgnoredVagrantMachines(object);
        loadVmsmConfig(object);
    }

    private void loadVMs(JSONObject obj) throws MalformedURLException {
        Storage.getVmList().clear();
        if (!obj.has("VMs")) return;
        for (Object ob : obj.getJSONArray("VMs")) {
            JSONObject json = (JSONObject)ob;
            VirtualMachine vm = VirtualMachine.fromJSON(json);
            Storage.getVmList().add(vm);
        }
    }

    private void loadCurrentVM(JSONObject obj) {
        ObservableList<VirtualMachine> vmList = Storage.getVmList();
        if (obj.has("current_vm")) {
            String currentVmName = obj.getString("current_vm");
            List<VirtualMachine> filtered = vmList.filtered(vm -> vm.getName().equals(currentVmName));
            if (filtered.size() == 1) {
                VM.set(filtered.get(0));
            }
            else if (vmList.size() > 0) {
                VM.set(vmList.get(0));
            }
        }
        else if (vmList.size() > 0) {
            VM.set(vmList.get(0));
        }
    }

    private void loadIgnoredVagrantMachines(JSONObject obj) {
        Storage.getIgnoredVagrantMachines().clear();
        if (obj.has("ignored_vagrant_machines")) {
            JSONArray array = obj.getJSONArray("ignored_vagrant_machines");
            array.iterator().forEachRemaining(entry -> Storage.getIgnoredVagrantMachines().add(entry.toString()));
        }
    }

    private void loadVmsmConfig(JSONObject obj) {
        Storage.config.clear();
        if (obj.has("config")) {
            Storage.config.putAll(obj.getJSONObject("config").toMap());
        }
    }

    @Override
    public void save(File file) throws IOException {
        file.delete();
        file.createNewFile();

        JSONObject root = new JSONObject();
        List<JSONObject> list = Storage.getVmList().stream()
                .map(VirtualMachine::toJSON)
                .collect(Collectors.toList());
        JSONArray vms = new JSONArray(list);
        root.put("VMs", vms);
        VM.ifNotNull(vm -> root.put("current_vm", vm.getName()));
        List<String> ignoredVagrants = Storage.getIgnoredVagrantMachines();
        if (ignoredVagrants != null && !ignoredVagrants.isEmpty()) {
            JSONArray array = new JSONArray(ignoredVagrants);
            root.put("ignored_vagrant_machines", array);
        }
        root.put("config", new JSONObject(Storage.config));

        PrintWriter pw = new PrintWriter(file);
        pw.println(root.toString());
        pw.close();
    }
}
