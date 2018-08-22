package eu.shooktea.vmsm.config;

import eu.shooktea.datamodel.DataModelList;
import eu.shooktea.datamodel.DataModelMap;
import eu.shooktea.datamodel.DataModelPrimitive;
import eu.shooktea.datamodel.DataModelValue;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class Yaml_1_0 implements YamlVersionInterface {
    @Override
    public void save(DataModelMap map) {
        saveVMs(map);
        saveCurrentVM(map);
        saveIgnoredVagrantMachines(map);
        saveVmsmConfig(map);
    }

    protected void saveVMs(DataModelMap map) {
        DataModelList vms = Storage.getVmList()
                .stream()
                .map(VirtualMachine::toMap)
                .collect(Collectors.toCollection(DataModelList::new));
        map.put("virtual machines", vms);
    }

    protected void saveCurrentVM(DataModelMap map) {
        VirtualMachine vm = VM.get();
        if (vm != null) {
            map.put("current VM", vm.getName());
        }
    }

    protected void saveIgnoredVagrantMachines(DataModelMap map) {
        map.put("ignored Vagrant machines", Storage.getIgnoredVagrantMachines());
    }

    protected void saveVmsmConfig(DataModelMap map) {
        map.put("VMSM config", Storage.oldConfig);
    }

    @Override
    public void load(DataModelMap map) {
        loadVMs(map);
        loadCurrentVM(map);
        loadIgnoredVagrantMachines(map);
        loadVmsmConfig(map);
    }

    protected void loadVMs(DataModelMap map) {
        Storage.getVmList().setAll(map
                .getOrDefault("virtual machines", new DataModelList())
                .toList()
                .stream()
                .map(DataModelValue::toMap)
                .map(VirtualMachine::fromMap)
                .collect(Collectors.toList())
        );
    }

    protected void loadCurrentVM(DataModelMap map) {
        ObservableList<VirtualMachine> vmList = Storage.getVmList();
        if (map.containsKey("current VM")) {
            String currentVmName = map.getString("current VM");
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

    protected void loadIgnoredVagrantMachines(DataModelMap map) {
        Storage.getIgnoredVagrantMachines().clear();
        Storage.getIgnoredVagrantMachines().addAll(map
                .getOrDefault("ignored Vagrant machines", new DataModelList())
                .toList()
                .stream()
                .map(DataModelValue::<String>toPrimitive)
                .map(DataModelPrimitive::getContent)
                .collect(Collectors.toList())
        );
    }

    protected void loadVmsmConfig(DataModelMap map) {
        Storage.oldConfig.clear();
        Storage.oldConfig.putAll(
                map.getOrDefault("VMSM config", new DataModelMap()).toMap()
        );
    }
}
