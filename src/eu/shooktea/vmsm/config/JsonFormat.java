package eu.shooktea.vmsm.config;

import eu.shooktea.datamodel.*;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JsonFormat extends AbstractFormat {
    @Override
    public void load(File file) {
        if (!file.exists() || !file.isFile()) return;
        DataModelMap map = JSON.instance().load(file);
        loadVMs(map);
        loadCurrentVM(map);
        loadIgnoredVagrantMachines(map);
        loadVmsmConfig(map);
    }

    private void loadVMs(DataModelMap map) {
        Storage.getVmList().setAll(
                map.getOrDefault("VMs", new DataModelList())
                .toList()
                .stream()
                .map(DataModelValue::toMap)
                .map(VirtualMachine::fromMap)
                .collect(Collectors.toList())
        );
    }

    private void loadCurrentVM(DataModelMap map) {
        ObservableList<VirtualMachine> vmList = Storage.getVmList();
        if (map.containsKey("current_vm")) {
            String currentVmName = map.getString("current_vm");
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

    private void loadIgnoredVagrantMachines(DataModelMap map) {
        Storage.getIgnoredVagrantMachines().clear();
        Storage.getIgnoredVagrantMachines().addAll(
                map.getOrDefault("ignored_vagrant_machines", new DataModelList())
                .toList()
                .stream()
                .map(DataModelValue::<String>toPrimitive)
                .map(DataModelPrimitive::getContent)
                .collect(Collectors.toList())
        );
    }

    private void loadVmsmConfig(DataModelMap map) {
        Storage.oldConfig.clear();
        Storage.oldConfig.putAll(
                map.getOrDefault("config", new DataModelMap()).toMap()
        );
    }

    @Override
    public void save(File file) throws IOException {
        file.delete();
        file.createNewFile();

        DataModelMap root = new DataModelMap();
        root.put("VMs",
                Storage.getVmList()
                .stream()
                .map(VirtualMachine::toMap)
                .collect(Collectors.toCollection(DataModelList::new))
        );
        VM.ifNotNull(vm -> root.put("current_vm", vm.getName()));
        root.put("ignored_vagrant_machines", Storage.getIgnoredVagrantMachines());
        root.put("config", Storage.oldConfig);
        JSON.instance().store(root, file);
    }

    @Override
    protected File createSaveFile(File directory) {
        return new File(directory, "config.json");
    }
}
