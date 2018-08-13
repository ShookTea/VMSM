package eu.shooktea.vmsm.module.dockercompose;

import com.amihaiemil.camel.YamlMapping;

public class Service {
    public Service(String name, YamlMapping mapping) {
        this.name = name;
        this.yaml = mapping;
    }

    @Override
    public String toString() {
        return "Service '" + name +"' = {\n" + yaml.toString() + "}";
    }

    private final String name;
    private final YamlMapping yaml;
}
