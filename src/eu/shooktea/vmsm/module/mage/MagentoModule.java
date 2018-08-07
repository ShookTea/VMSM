package eu.shooktea.vmsm.module.mage;

import eu.shooktea.vmsm.Toolkit;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of single module installed on Magento.
 * <p>
 * Every module in Magento is stored in one of three code pools: {@code core}, {@code community} and {@code local}. Core
 * code pool is a base of Magento and shouldn't be edited by module programmers. Community code pool contains modules
 * downloaded for free or bought on Magento repository or other webpages. Local code pool contains modules used only
 * in current installation of Magento.
 * <p>
 * Modules' full names consists of two parts connected by underscore, i.e. {@code Mage_Adminhtml}. First part is
 * module's namespace and second is module's name.
 * <p>
 * Every module has a version saved in configuration file and in database. If these two version are not equal, setup
 * script is launched to update module to newer version.
 */
public class MagentoModule {
    /**
     * Main constructor.
     * @param codePool module's code pool
     * @param namespace module's namespace
     * @param name module's name
     * @param installedVersion module's installed version
     * @param xmlVersion module's XML version
     * @param rootFile module's root file
     * @param setupTag module's setup tag
     * @see MagentoModule
     */
    public MagentoModule(String codePool, String namespace, String name, String installedVersion, String xmlVersion, File rootFile, String setupTag) {
        this.codePool = new SimpleStringProperty(codePool);
        this.namespace = new SimpleStringProperty(namespace);
        this.name = new SimpleStringProperty(name);
        this.installedVersion = new SimpleStringProperty(installedVersion);
        this.xmlVersion = new SimpleStringProperty(xmlVersion);
        this.rootFile = rootFile;
        this.setupDir = setupTag == null ? null : new File(rootFile, "sql/" + setupTag);
    }

    /**
     * Returns path to module in Magento directory. If module's code pool is {@code CP}, namespace is {@code NS}
     * and name is {@code NAME}, then that method will return {@code /app/code/CP/NS/NAME}.
     * @return path to module.
     */
    public String getDisplayRootFile() {
        return "/app/code/" + getCodePool() + "/" + getNamespace() + "/" + getName();
    }

    /**
     * Opens directory with root directory.
     * @see #getDisplayRootFile()
     */
    public void openRootDir() {
        try {
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().open(rootFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates list of all versions of that module in line. Order of versions is based on setup files' names. Version
     * saved in configuration file will have note "in config.xml", version saved in database will have note "installed".
     * @return list of versions.
     */
    public List<String> createVersionList() {
        List<String> versionList = new ArrayList<>();
        if (setupDir == null || !setupDir.exists() || !setupDir.isDirectory() || setupDir.listFiles() == null || setupDir.listFiles().length == 0)
            return versionList;

        for (File file : setupDir.listFiles()) {
            String fileName = file.getName();
            if (!fileName.contains("upgrade")) continue;
            String name = fileName.toLowerCase();
            name = name.replaceAll("^.*upgrade-([^-]*)-([^-]*)\\..*$", "$1:$2");
            versionList.add(name);
        }

        versionList = Toolkit.parseLine(versionList);
        int installedIndex = versionList.indexOf(installedVersion.getValue());
        int xmlIndex = versionList.indexOf(xmlVersion.getValue());
        if (installedIndex == xmlIndex && installedIndex != -1) {
            versionList.set(installedIndex, versionList.get(installedIndex) + " (installed/in config.xml)");
        }
        else {
            if (installedIndex != -1) {
                versionList.set(installedIndex, versionList.get(installedIndex) + " (installed)");
            }
            if (xmlIndex != -1) {
                versionList.set(xmlIndex, versionList.get(xmlIndex) + " (in config.xml)");
            }
        }
        return versionList;
    }

    /**
     * Returns setup config name. Module version stored in database is identified by that name.
     * @return setup config name.
     */
    public String getConfigName() {
        return setupDir.getName();
    }

    private ReadOnlyStringProperty codePool;
    private ReadOnlyStringProperty namespace;
    private ReadOnlyStringProperty name;
    private StringProperty installedVersion;
    private ReadOnlyStringProperty xmlVersion;
    private File rootFile;
    private File setupDir;

    /**
     * Returns module's code pool.
     * @return code pool
     */
    public String getCodePool() {
        return codePoolProperty().get();
    }

    /**
     * Returns module's code pool property.
     * @return code pool property
     */
    public ReadOnlyStringProperty codePoolProperty() {
        return codePool;
    }

    /**
     * Returns module's namespace.
     * @return namespace
     */
    public String getNamespace() {
        return namespaceProperty().get();
    }

    /**
     * Returns module's namespace property.
     * @return namespace property
     */
    public ReadOnlyStringProperty namespaceProperty() {
        return namespace;
    }

    /**
     * Returns module's name.
     * @return name
     */
    public String getName() {
        return nameProperty().get();
    }

    /**
     * Returns module's name property.
     * @return name property
     */
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    /**
     * Returns module's installed version, loaded from database.
     * @return installed version
     */
    public String getInstalledVersion() {
        return installedVersionProperty().get();
    }

    /**
     * Sets new module's installed version. It will not send that version to database.
     * @param version new installed version
     */
    public void setInstalledVersion(String version) {
        installedVersionProperty().setValue(version);
    }

    /**
     * Returns module's installed version property, loaded from database. Changing value in that property will not
     * send any new version to database.
     * @return installed version property.
     */
    public StringProperty installedVersionProperty() {
        return installedVersion;
    }

    /**
     * Returns module's configuration version, loaded from {@code config.xml} file.
     * @return configuration version
     */
    public String getXmlVersion() {
        return xmlVersion.get();
    }

    /**
     * Returns module's configuration version property.
     * @return configuration version property
     */
    public ReadOnlyStringProperty xmlVersionProperty() {
        return xmlVersion;
    }
}
