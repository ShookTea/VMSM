package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.yaml.YamlMap;
import eu.shooktea.yaml.YamlValue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;

public class Service {
    public Service(String name, YamlMap map) {
        this.yaml = map;
        this.name = new SimpleStringProperty(name);
        this.sourceType = new SimpleObjectProperty<>(ServiceSource.fromYaml(yaml));
        this.source = new SimpleStringProperty(getSourceType().source(yaml));
    }

    public Service(Map.Entry<String, YamlValue> entry) {
        this(entry.getKey(), entry.getValue().toMap());
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

    private final StringProperty name;
    private final ObjectProperty<ServiceSource> sourceType;
    private final StringProperty source;
    private final YamlMap yaml;

    public enum ServiceSource {
        BUILD, IMAGE;

        public String source(YamlMap yaml) {
            switch(this) {
                case BUILD: return yaml.get("build").toYamlObject().toString();
                case IMAGE: return yaml.get("image").toYamlObject().toString();
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

        public static ServiceSource fromYaml(YamlMap yaml) {
            if (yaml.containsKey("image")) return IMAGE;
            if (yaml.containsKey("build")) return BUILD;
            return null;
        }

        public static ObservableList<ServiceSource> listValues() {
            return FXCollections.observableArrayList(ServiceSource.values());
        }
    }
}
