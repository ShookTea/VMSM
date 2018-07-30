package eu.shooktea.vmsm.module;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;

public class MagentoModule {
    public MagentoModule(String codePool, String namespace, String name, String installedVersion, String xmlVersion) {
        this.codePool = new SimpleStringProperty(codePool);
        this.namespace = new SimpleStringProperty(namespace);
        this.name = new SimpleStringProperty(name);
        this.installedVersion = new SimpleStringProperty(installedVersion);
        this.xmlVersion = new SimpleStringProperty(xmlVersion);
    }

    private ReadOnlyStringProperty codePool;
    private ReadOnlyStringProperty namespace;
    private ReadOnlyStringProperty name;
    private ReadOnlyStringProperty installedVersion;
    private ReadOnlyStringProperty xmlVersion;

    public String getCodePool() {
        return codePool.get();
    }

    public ReadOnlyStringProperty codePoolProperty() {
        return codePool;
    }

    public String getNamespace() {
        return namespace.get();
    }

    public ReadOnlyStringProperty namespaceProperty() {
        return namespace;
    }

    public String getName() {
        return name.get();
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    public String getInstalledVersion() {
        return installedVersion.get();
    }

    public ReadOnlyStringProperty installedVersionProperty() {
        return installedVersion;
    }

    public String getXmlVersion() {
        return xmlVersion.get();
    }

    public ReadOnlyStringProperty xmlVersionProperty() {
        return xmlVersion;
    }
}
