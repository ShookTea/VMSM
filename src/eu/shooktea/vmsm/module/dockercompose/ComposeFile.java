package eu.shooktea.vmsm.module.dockercompose;

import com.amihaiemil.camel.Yaml;
import com.amihaiemil.camel.YamlMapping;
import com.amihaiemil.camel.YamlMappingBuilder;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ComposeFile {
    public ComposeFile() throws IOException {
        VirtualMachine vm = VM.getOrThrow();
        this.yamlFile = ((eu.shooktea.vmsm.vmtype.DockerCompose)vm.getType()).getDockerComposeFile(vm);
        this.mapping = Yaml.createYamlInput(yamlFile).readYamlMapping();
        this.services = FXCollections.observableArrayList();
        parseYaml();
    }

    public ObservableList<Service> getServices() {
        return services;
    }

    private void parseYaml() {
        String[] possibleKeys = getPossibleKeys();
        YamlMapping servicesYaml = mapping.yamlMapping("services");
        services.clear();
        for (String key : possibleKeys) {
            YamlMapping service = servicesYaml.yamlMapping(key);
            if (service != null) services.add(new Service(key, service));
        }
    }

    private String[] getPossibleKeys() {
        List<String> ret = new ArrayList<>();
        String[] text = mapping.toString().split("\n");
        for (String line : text) {
            line = line.trim();
            if (line.endsWith(":")) ret.add(line.substring(0, line.length() - 1));
        }
        return ret.toArray(new String[0]);
    }

    public void save() throws IOException {
        YamlMappingBuilder yaml = Yaml.createYamlMappingBuilder();
        for (String key : getPossibleKeys()) if (!key.equals("services")) {
            if (mapping.string(key) != null) yaml.add(key, mapping.string(key));
            else if (mapping.yamlMapping(key) != null) yaml.add(key, mapping.yamlMapping(key));
            else if (mapping.yamlSequence(key) != null) yaml.add(key, mapping.yamlSequence(key));
        }

        for (Service service : services) {
            yaml.add(service.getName(), service.toYaml());
        }

        YamlMapping newMapping = yaml.build();
        PrintWriter writer = new PrintWriter(yamlFile);
        writer.println(newMapping);
        writer.close();
    }

    private final YamlMapping mapping;
    private final File yamlFile;
    private final ObservableList<Service> services;
}
