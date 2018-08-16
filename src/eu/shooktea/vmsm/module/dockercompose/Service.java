package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.datamodel.DataModelList;
import eu.shooktea.datamodel.DataModelMap;
import eu.shooktea.datamodel.DataModelPrimitive;
import eu.shooktea.datamodel.DataModelValue;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

public class Service {
    public Service(String name, DataModelMap map, ComposeFile composeFile) {
        this.yaml = map;
        this.compose = composeFile;
        this.name = new SimpleStringProperty(name);
        this.sourceType = new SimpleObjectProperty<>(ServiceSource.fromYaml(yaml));
        this.source = new SimpleStringProperty(getSourceType().source(yaml));
    }

    public Service(String name, ComposeFile composeFile) {
        this.yaml = new DataModelMap();
        this.compose = composeFile;
        this.name = new SimpleStringProperty(name);
        this.sourceType = new SimpleObjectProperty<>();
        this.source = new SimpleStringProperty();
    }

    public DataModelMap toYamlMap() {
        yaml.removeKeys("image", "build", "depends_on", "links");
        if (getSourceType() == ServiceSource.BUILD) {
            yaml.put("build", new DataModelPrimitive<>(getSource()));
        }
        else if (getSourceType() == ServiceSource.IMAGE){
            yaml.put("image", new DataModelPrimitive<>(getSource()));
        }
        if (!getDependencies().isEmpty()) {
            yaml.put("depends_on", new DataModelList(
                    getDependencies()
                    .stream()
                    .map(Service::getName)
                    .map(DataModelPrimitive::new)
                    .collect(Collectors.toList())
            ));
        }
        if (!getLinks().isEmpty()) {
            yaml.put("links", new DataModelList(
                    getLinks()
                    .stream()
                    .map(Service::getName)
                    .map(DataModelPrimitive::new)
                    .collect(Collectors.toList())
            ));
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

    public ListProperty<Service> linksProperty() {
        if (links == null) links = createFromList("links", yaml, compose);
        return links;
    }

    public ObservableList<Service> getLinks() {
        return linksProperty().get();
    }

    public void setLinks(ObservableList<Service> services) {
        linksProperty().set(services);
    }

    public ListProperty<Service> dependenciesProperty() {
        if (dependencies == null) dependencies = createFromList("depends_on", yaml, compose);
        return dependencies;
    }

    public ObservableList<Service> getDependencies() {
        return dependenciesProperty().get();
    }

    public void setDependencies(ObservableList<Service> services) {
        dependenciesProperty().set(services);
    }

    private final StringProperty name;
    private final ObjectProperty<ServiceSource> sourceType;
    private final StringProperty source;
    private ListProperty<Service> links = null;
    private ListProperty<Service> dependencies = null;
    private final DataModelMap yaml;
    private final ComposeFile compose;

    private static SimpleListProperty<Service> createFromList(String key, DataModelMap parent, ComposeFile compose) {
        return new SimpleListProperty<>(FXCollections.observableArrayList(
                parent.getOrDefault(key, new DataModelList())
                        .toList()
                        .stream()
                        .map(DataModelValue::<String>toPrimitive)
                        .map(DataModelPrimitive::getContent)
                        .map(compose::byName)
                        .collect(Collectors.toList())
        ));
    }

    public enum ServiceSource {
        BUILD, IMAGE;

        public String source(DataModelMap yaml) {
            switch(this) {
                case BUILD: return yaml.getString("build");
                case IMAGE: return yaml.getString("image");
                default: return null;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case BUILD: return "Dockerfile";
                case IMAGE: return "Docker Hub";
                default: return null;
            }
        }

        public static ServiceSource fromYaml(DataModelMap yaml) {
            if (yaml.containsKey("image")) return IMAGE;
            if (yaml.containsKey("build")) return BUILD;
            return null;
        }

        public static ObservableList<ServiceSource> listValues() {
            return FXCollections.observableArrayList(ServiceSource.values());
        }
    }
}
