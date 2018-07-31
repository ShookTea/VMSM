package eu.shooktea.vmsm.module;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import org.w3c.dom.Element;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MagentoModule {
    public MagentoModule(String codePool, String namespace, String name, String installedVersion, String xmlVersion, File rootFile, Element config, String setupTag) {
        this.codePool = new SimpleStringProperty(codePool);
        this.namespace = new SimpleStringProperty(namespace);
        this.name = new SimpleStringProperty(name);
        this.installedVersion = new SimpleStringProperty(installedVersion);
        this.xmlVersion = new SimpleStringProperty(xmlVersion);
        this.config = config;
        this.rootFile = rootFile;
    }

    public String getDisplayRootFile() {
        return "/app/code/" + getCodePool() + "/" + getNamespace() + "/" + getName();
    }

    public void openConfig() {
        try {
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().open(new File(rootFile, "etc" + File.separator + "config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openRootDir() {
        try {
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().open(rootFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> createVersionList() {
        List<String> versionList = new ArrayList<>();

        return versionList;
    }

    private ReadOnlyStringProperty codePool;
    private ReadOnlyStringProperty namespace;
    private ReadOnlyStringProperty name;
    private ReadOnlyStringProperty installedVersion;
    private ReadOnlyStringProperty xmlVersion;
    private Element config;
    private File rootFile;

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
