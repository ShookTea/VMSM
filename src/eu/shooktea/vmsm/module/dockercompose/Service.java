package eu.shooktea.vmsm.module.dockercompose;

import com.amihaiemil.camel.YamlMapping;

public class Service {
    public Service(String name, YamlMapping mapping) {
        this.name = name;
        this.yaml = mapping;
    }

    private final String name;
    private final YamlMapping yaml;
}
