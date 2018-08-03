package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.mage.MagentoConfig;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Module representing Magento e-commerce.
 */
public class Magento extends Module {

    @Override
    public String getName() {
        return "Magento";
    }

    @Override
    public String getDescription() {
        return "Open-source e-commerce platform written in PHP";
    }

    @Override
    public Optional<Runnable> openConfigWindow() {
        return Optional.of(MagentoConfig::openMagentoConfig);
    }

    @Override
    public void loopUpdate() {
        VirtualMachine vm = VM.getOrThrow();
        String rootPath = getStringSetting(vm, "path");
        if (rootPath == null) return;
        File root = new File(rootPath);
        if (!root.exists()) return;

        File reports = new File(root, "var/report");
        if (reports.exists()) MagentoReport.update(this, vm, reports);
    }

    private String getAdminAddress(VirtualMachine vm) {
        return getConfigFromLocalXmlFile(vm, "admin/routers/adminhtml/args/frontName", "admin");
    }

    public String getConfigFromLocalXmlFile(VirtualMachine vm, String pathString, String defaultValue) {
        try {
            File localXmlFile = getLocalXmlFile(vm);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(localXmlFile);
            Element config = doc.getDocumentElement();
            config.normalize();
            String[] path = pathString.split("/");
            for (String s : path) {
                config = (Element)config.getElementsByTagName(s).item(0);
            }
            return config.getTextContent().trim();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
            return defaultValue;
        } catch (Throwable e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private File getLocalXmlFile(VirtualMachine vm) throws IOException {
        String rootPath = getStringSetting(vm, "path");
        if (rootPath == null) throw new IOException("rootPath == null");
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) throw new IOException("rootFile " + rootFile.toString() + "doesn't exist");
        File localXmlFile = new File(rootFile, "app/etc/local.xml");
        if (!localXmlFile.exists()) throw new IOException("local xml file " + localXmlFile.toString() + " doesn't exist");
        return localXmlFile;
    }

    @Override
    public Optional<List<ImageView>> getQuickGuiButtons() {
        ImageView deleteCache = Toolkit.createQuickGuiButton("trash_full.png", "Delete cache files");
        deleteCache.setOnMouseClicked(e -> deleteAllInVar(VM.getOrThrow(), DeleteDir.CACHE));

        return Optional.of(Arrays.asList(deleteCache));
    }

    @Override
    public Optional<MenuItem> getMenuItem() {
        MenuItem item = new MenuItem("Magento test");
        return Optional.of(item);
    }

    /**
     * Removes all files in {@code /var/X} directories, where X are loaded from arguments.
     * @param vm virtual machine that contains file to be removed
     * @param toDelete subdirectories in {@code /var} directory that should be cleaned.
     */
    public void deleteAllInVar(VirtualMachine vm, DeleteDir toDelete) {
        String mainPath = getStringSetting(vm, "path");
        if (mainPath == null) return;

        File root = new File(mainPath);
        if (!root.exists() || !root.isDirectory()) return;

        File var = new File(root, "var");
        if (!var.exists() || !var.isDirectory()) return;

        boolean somethingWrongHappen = false;

        for (String subdir : toDelete.path) {
            File toRemoveDir = new File(var, subdir);
            if (toRemoveDir.exists() && toRemoveDir.isDirectory()) {
                for (File file : toRemoveDir.listFiles()) {
                    if (delete(file)) somethingWrongHappen = true;
                }
            }
        }

        if (somethingWrongHappen)
            View.showMessage("Failed to delete files", Color.RED);
        else
            View.showMessage(toDelete.deleteInfo, Color.GREEN);
    }

    private static boolean delete(File file) {
        boolean somethingWrong = false;
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                if (!delete(c)) somethingWrong = true;
            }
        }
        if (!file.delete()) {
            file.deleteOnExit();
            somethingWrong = true;
        }
        return somethingWrong;
    }

    public Task<ObservableList<MagentoModule>> createModuleLoaderTask() {
        return new MagentoModuleLoader(this, VM.getOrThrow());
    }

    private static void loginAsAdmin() {
        VirtualMachine vm = VM.getOrThrow();
        Magento magento = Module.getModuleByName("Magento");
        String address = magento.getAdminAddress(vm);
        String currentAddress = vm.getPageRoot().toString();
        if (!currentAddress.endsWith("/")) currentAddress = currentAddress + "/";
        currentAddress = currentAddress + address;

        String login = magento.getStringSetting(vm, "adm_login");
        String pass = magento.getStringSetting(vm, "adm_pass");

        //TODO: Generate login file and redirect to it via View.openURL(new URL(currentAddress))
    }

    @Override
    public int getSortValue() {
        return 0;
    }

    public enum DeleteDir {
        CACHE("Cache deleted", "cache"),
        LOGS("Logs deleted", "log"),
        REPORTS("Reports deleted", "report"),
        SESSION("Sessions deleted", "session"),
        ALL("Unnecessary /var files deleted", "cache", "log", "report", "session");


        DeleteDir(String deleteInfo, String... path) {
            this.path = path;
            this.deleteInfo = deleteInfo;
        }

        public final String[] path;
        public final String deleteInfo;
    }
}
