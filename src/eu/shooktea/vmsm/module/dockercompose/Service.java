package eu.shooktea.vmsm.module.dockercompose;

import com.amihaiemil.camel.Yaml;
import com.amihaiemil.camel.YamlMapping;
import com.amihaiemil.camel.YamlMappingBuilder;
import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Service {
    public Service(String name, YamlMapping mapping) {
        this.yaml = mapping;
        this.name = new SimpleStringProperty(name);
        this.sourceType = new SimpleObjectProperty<>(ServiceSource.fromYaml(yaml));
        this.source = new SimpleStringProperty(getSourceType().source(yaml));
    }

    public YamlMapping toYaml() {
        YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();
        for (String key : getPossibleKeys()) {
            if (key.equals("build") || key.equals("image")) continue;
            if (yaml.string(key) != null) builder.add(key, yaml.string(key));
            else if (yaml.yamlSequence(key) != null) builder.add(key, yaml.yamlSequence(key));
            else if (yaml.yamlMapping(key) != null) builder.add(key, yaml.yamlMapping(key));
        }
        if (getSourceType() == ServiceSource.IMAGE)
            builder.add("image", getSource());
        else if (getSourceType() == ServiceSource.BUILD)
            builder.add("build", getSource());

        return builder.build();
    }

    @Override
    public String toString() {
        return getName();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String newName) {
        nameProperty().set(newName);
    }

    public ObjectProperty<ServiceSource> sourceTypeProperty() {
        return sourceType;
    }

    public ServiceSource getSourceType() {
        return sourceTypeProperty().get();
    }

    public void setSourceType(ServiceSource newSource) {
        sourceTypeProperty().set(newSource);
    }

    public StringProperty sourceProperty() {
        return source;
    }

    public String getSource() {
        return sourceProperty().toString();
    }

    public void setSource(String newSource) {
        sourceProperty().set(newSource);
    }

    private String[] getPossibleKeys() {
        List<String> ret = new ArrayList<>();
        String[] text = yaml.toString().split("\n");
        for (String line : text) {
            line = line.trim();
            if (line.endsWith(":")) ret.add(line.substring(0, line.length() - 1));
            else if (line.contains(":")) ret.add(line.split(":")[0].trim());
        }
        return ret.toArray(new String[0]);
    }

    private final StringProperty name;
    private final ObjectProperty<ServiceSource> sourceType;
    private final StringProperty source;
    private final YamlMapping yaml;

    public enum ServiceSource {
        BUILD, IMAGE;

        public String source(YamlMapping yaml) {
            switch (this) {
                case BUILD: return yaml.string("build");
                case IMAGE: return yaml.string("image");
                default: return null;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case BUILD: return "Dockerfile";
                case IMAGE: return "Image";
                default: return null;
            }
        }

        public static ServiceSource fromYaml(YamlMapping yaml) {
            if (yaml.string("image") != null) return IMAGE;
            if (yaml.string("build") != null) return BUILD;
            return null;
        }

        public static ObservableList<ServiceSource> listValues() {
            return FXCollections.observableArrayList(ServiceSource.values());
        }
    }
}
