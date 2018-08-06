package eu.shooktea.vmsm.module.mage;

import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.mysql.MySQL;
import eu.shooktea.vmsm.module.mysql.SqlConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.ResultSet;
import java.util.*;

public class ModuleLoader extends Task<ObservableList<MagentoModule>> {

    public ModuleLoader(Magento magento, VirtualMachine vm) {
        this.magento = magento;
        this.vm = vm;
        this.versions = new HashMap<>();
    }

    private final Magento magento;
    private final VirtualMachine vm;

    @Override
    protected ObservableList<MagentoModule> call() throws Exception {
        updateProgress(-1, -1);

        ObservableList<MagentoModule> list = FXCollections.observableArrayList();
        String path = magento.getStringSetting(vm, "path");
        if (path == null) {
            updateProgress(1, 1);
            return list;
        }

        File dir = createDirFile(path);
        File[] files = dir.listFiles();
        if (files == null) {
            updateProgress(1, 1);
            return list;
        }

        int count = 0;
        int max = files.length;
        updateProgress(count, max);
        getSQL();
        for (File f : files) {
            List<MagentoModule> modules = loadMagentoModule(f);
            list.addAll(modules);
            updateProgress(++count, max);
        }
        return list;
    }

    private File createDirFile(String path) {
        if (!path.endsWith(File.separator)) path = path + File.separator;
        path = path + "app" + File.separator + "etc" + File.separator + "modules";
        File dir = new File(path);
        return dir;
    }

    private List<MagentoModule> loadMagentoModule(File file) {
        List<MagentoModule> ret = new ArrayList<>();
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(file);
            Element config = doc.getDocumentElement();
            config.normalize();
            Element modules = (Element)config.getElementsByTagName("modules").item(0);
            NodeList list = modules.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                org.w3c.dom.Node n = list.item(i);
                if (n instanceof Element) {
                    Element module = (Element)n;
                    String[] fullModuleName = module.getTagName().split("_");
                    String codePool = module.getElementsByTagName("codePool").item(0).getTextContent();
                    File rootFile = createRootFile(codePool, fullModuleName[0], fullModuleName[1]);
                    Element configElement = createConfigElement(rootFile);
                    String setupTagName = getSetupTagName(configElement, fullModuleName[0], fullModuleName[1]);
                    String sqlVersion = getSqlVersion(setupTagName);
                    String xmlVersion = getXmlVersion(fullModuleName[0], fullModuleName[1], configElement);
                    ret.add(new MagentoModule(codePool, fullModuleName[0], fullModuleName[1], sqlVersion, xmlVersion, rootFile, configElement, setupTagName));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return ret;
    }

    private File createRootFile(String codePool, String namespace, String name) {
        String path = magento.getStringSetting(vm, "path");
        if (!path.endsWith(File.separator)) path = path + File.separator;
        path = path +
                "app" + File.separator +
                "code" + File.separator +
                codePool + File.separator +
                namespace + File.separator +
                name;
        File file = new File(path);
        return file;
    }

    private Element createConfigElement(File file) {
        try {
            String path = "etc" + File.separator + "config.xml";
            file = new File(file, path);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(file);
            Element config = doc.getDocumentElement();
            config.normalize();
            return config;
        } catch (Exception ex) {
            return null;
        }
    }

    private String getXmlVersion(String namespace, String name, Element config) {
        String xml = "nd.";
        try {
            if (config == null) throw new Exception("Catch it and return nd./nd.");
            Element modules = (Element)config.getElementsByTagName("modules").item(0);
            Element module = (Element)modules.getElementsByTagName(namespace + "_" + name).item(0);
            xml = module.getElementsByTagName("version").item(0).getTextContent();
        } catch (Exception ex) {}
        return xml;
    }

    private String getSqlVersion(String tagName) {
        if (tagName == null) return "nd.";
        return versions.getOrDefault(tagName, "nd.");
    }

    private String getSetupTagName(Element config, String namespace, String name) {
        String tagName = null;
        try {
            if (config == null) throw new Exception("Catch it and return nd./nd.");
            Element modules = (Element)config.getElementsByTagName("modules").item(0);
            Element module = (Element)modules.getElementsByTagName(namespace + "_" + name).item(0);


            Element global = (Element)config.getElementsByTagName("global").item(0);
            Element resources = (Element)global.getElementsByTagName("resources").item(0);

            for (int i = 0; i < resources.getChildNodes().getLength(); i++) {
                Node n = resources.getChildNodes().item(i);
                if (!(n instanceof Element)) continue;
                Element elem = (Element)n;
                if (elem.getElementsByTagName("setup").getLength() != 1) continue;
                tagName = elem.getTagName();
                break;
            }
        } catch (Exception ex) {}
        return tagName;
    }

    private void getSQL() {
        try {
            MySQL sql = MySQL.getModuleByName("MySQL");
            if (!sql.isInstalled(vm)) return;
            SqlConnection connection = sql.createConnection();
            connection.open();
            ResultSet set = (ResultSet) connection.query("SELECT `code` AS \"module\", `version` AS \"version\" FROM `core_resource`");
            while (set.next()) {
                versions.put(set.getString("module"), set.getString("version"));
            }
            set.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map<String, String> versions;
}
