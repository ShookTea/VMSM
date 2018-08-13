package eu.shooktea.vmsm.module.dockercompose;

import com.amihaiemil.camel.YamlMapping;

import java.util.ArrayList;
import java.util.List;

public class ComposeFile {
    public ComposeFile(YamlMapping mapping) {
        this.mapping = mapping;
        this.services = new ArrayList<>();
        parseYaml();
    }

    public List<Service> getServices() {
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

    public void test() {
        for (Service service : getServices()) {
            System.out.println(service);
        }
    }

    private final YamlMapping mapping;
    private final List<Service> services;
}
