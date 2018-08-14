package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.yaml.YamlList;
import eu.shooktea.yaml.YamlMap;
import eu.shooktea.yaml.YamlPrimitive;
import eu.shooktea.yaml.YamlValue;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class Service {
    public Service(String name, YamlMap map, ComposeFile composeFile) {
        this.yaml = map;
        this.compose = composeFile;
        this.name = new SimpleStringProperty(name);
        this.sourceType = new SimpleObjectProperty<>(ServiceSource.fromYaml(yaml));
        this.source = new SimpleStringProperty(getSourceType().source(yaml));
        this.links = createFromList("links", yaml, compose);
        this.dependencies = createFromList("depends_on", yaml, compose);
    }

    private static SimpleListProperty<Service> createFromList(String key, YamlMap parent, ComposeFile compose) {
        return new SimpleListProperty<>(FXCollections.observableArrayList(
                parent.getOrDefault(key, new YamlList())
                .toList()
                .stream()
                .map(YamlValue::toPrimitive)
                .map(YamlPrimitive::toYamlObject)
                .map(Object::toString)
                .map(compose::byName)
                .collect(Collectors.toList())
        ));
    }

    public YamlMap toYamlMap() {
        if (getSourceType() == ServiceSource.BUILD) {
            yaml.remove("image");
            yaml.put("build", new YamlPrimitive<>(getSource()));
        }
        else if (getSourceType() == ServiceSource.IMAGE){
            yaml.remove("build");
            yaml.put("image", new YamlPrimitive<>(getSource()));
        }
        return yaml;
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
        return sourceProperty().get();
    }

    public void setSource(String newSource) {
        sourceProperty().set(newSource);
    }

    private final StringProperty name;
    private final ObjectProperty<ServiceSource> sourceType;
    private final ListProperty<Service> links;
    private final ListProperty<Service> dependencies;
    private final StringProperty source;
    private final YamlMap yaml;
    private final ComposeFile compose;

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
