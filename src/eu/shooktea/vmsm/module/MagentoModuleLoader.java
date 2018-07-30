package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.VirtualMachine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MagentoModuleLoader extends Task<ObservableList<MagentoModule>> {

    public MagentoModuleLoader(Magento magento, VirtualMachine vm) {
        this.magento = magento;
        this.vm = vm;
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
                    ret.add(new MagentoModule(codePool, fullModuleName[0], fullModuleName[1], "INSTALLED", "XML"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return ret;
    }

}
