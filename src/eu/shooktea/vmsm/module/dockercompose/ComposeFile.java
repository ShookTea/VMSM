package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.yaml.*;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class ComposeFile {
    public ComposeFile() throws IOException {
        VirtualMachine vm = VM.getOrThrow();
        this.yamlFile = ((eu.shooktea.vmsm.vmtype.DockerCompose)vm.getType()).getDockerComposeFile(vm);
        this.mapping = YAML.instance().load(yamlFile).toMap();
        this.services = FXCollections.observableArrayList();
        parseYaml();
    }

    public ObservableList<Service> getServices() {
        return services;
    }

    public Service byName(String name) {
        return getServices().stream().filter(s -> s.getName().equals(name)).findAny().get();
    }

    private void parseYaml() {
        services.setAll(
                mapping.getOrDefault("services", new YamlMap())
                .toMap()
                .stream()
                .map(e -> new Service(e.getKey(), e.getValue().toMap(), this))
                .collect(Collectors.toList())
        );
    }

    public void save() throws IOException {
        YamlMap newServices = new YamlMap();
        for (Service service : services) {
            newServices.put(service.getName(), service.toYamlMap());
        }
        mapping.put("services", newServices);
        PrintWriter writer = new PrintWriter(yamlFile);
        YAML.instance().toWriter(mapping, writer);
    }

    private final YamlMap mapping;
    private final File yamlFile;
    private final ObservableList<Service> services;
}
