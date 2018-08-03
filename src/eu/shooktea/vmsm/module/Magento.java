package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.mage.CreateNewAdmin;
import eu.shooktea.vmsm.view.controller.mage.MagentoConfig;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
        VirtualMachine vm = VM.getOrThrow();
        List<ImageView> ret = new ArrayList<>();

        ImageView deleteCache = Toolkit.createQuickGuiButton("trash_full.png", "Delete cache files");
        deleteCache.setOnMouseClicked(e -> deleteAllInVar(VM.getOrThrow(), DeleteDir.CACHE));
        ret.add(deleteCache);

        ImageView loginAsAdmin = Toolkit.createQuickGuiButton("user.png", "Open admin panel");
        loginAsAdmin.setOnMouseClicked(e -> loginAsAdmin(vm, getStringSetting(vm, "adm_login")));
        ret.add(loginAsAdmin);

        return Optional.of(ret);
    }

    @Override
    public Optional<MenuItem> getMenuItem() {
        VirtualMachine vm = VM.getOrThrow();
        Menu root = new Menu("Magento", Toolkit.createMenuImage("magento.png"));

        Menu delete = new Menu("Remove", Toolkit.createMenuImage("trash_full.png"));
        delete.getItems().addAll(
                createDelete(DeleteDir.ALL, "All"),
                new SeparatorMenuItem(),
                createDelete(DeleteDir.CACHE, "Cache files"),
                createDelete(DeleteDir.LOGS, "Log files"),
                createDelete(DeleteDir.REPORTS, "Exception report files"),
                createDelete(DeleteDir.SESSION, "User sessions")
        );

        MenuItem autologin = new MenuItem("Open administrator panel", Toolkit.createMenuImage("user.png"));
        autologin.setOnAction(e -> loginAsAdmin(vm, getStringSetting(vm, "adm_login")));

        MenuItem newAdmin = new MenuItem("Create new admin account...");
        newAdmin.setOnAction(CreateNewAdmin::openAdminCreationWindow);

        MenuItem openConfig = new MenuItem("Magento configuration...", Toolkit.createMenuImage("run.png"));
        openConfig.setOnAction(MagentoConfig::openMagentoConfig);


        root.getItems().addAll(
                delete,
                new SeparatorMenuItem(),
                autologin,
                newAdmin,
                new SeparatorMenuItem(),
                openConfig
        );
        return Optional.of(root);
    }

    private MenuItem createDelete(DeleteDir dir, String text) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(e -> deleteAllInVar(VM.getOrThrow(), dir));
        return item;
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

    private void loginAsAdmin(VirtualMachine vm) {
        try {
            String address = getAdminAddress(vm);
            URL root = vm.getPageRoot();
            URL newPage = new URL(root, address);
            View.openURL(newPage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void loginAsAdmin(VirtualMachine vm, String login) {
        if (login == null) {
            loginAsAdmin(vm);
            return;
        }
        String fileName = "vmsm_autologin_" + System.currentTimeMillis() + ".php";
        String mainPath = getStringSetting(vm, "path");
        if (mainPath == null) return;

        File root = new File(mainPath);
        if (!root.exists() || !root.isDirectory()) return;

        File codeFile = new File(root, fileName);
        try {
            codeFile.createNewFile();
            PrintWriter pw = new PrintWriter(codeFile);
            pw.println(openAdminCode);
            pw.close();
            URL fileUrl = new URL(vm.getPageRoot(), fileName);
            URI oldUri = fileUrl.toURI();
            String newQuery = oldUri.getQuery();
            String append = "username=" + login;
            if (newQuery == null) {
                newQuery = append;
            }
            else {
                newQuery += "&" + append;
            }
            URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery, oldUri.getFragment());
            View.openURL(newUri);
            codeFile.deleteOnExit();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            if (codeFile.exists()) if (!codeFile.delete()) codeFile.deleteOnExit();
        }
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

    private static final String openAdminCode =
                    "<?php\n" +
                    "$username = $_GET['username'];\n" +
                    "require_once 'app/Mage.php';\n" +
                    "umask(0);\n" +
                    "$app = Mage::app('default');\n" +
                    "Mage::getSingleton('core/session', array('name' => 'adminhtml'));\n" +
                    "// supply username\n" +
                    "$user = Mage::getModel('admin/user')->loadByUsername($username);\n" +
                    "if (Mage::getSingleton('adminhtml/url')->useSecretKey()) {\n" +
                    "    Mage::getSingleton('adminhtml/url')->renewSecretUrls();\n" +
                    "}\n" +
                    "$session = Mage::getSingleton('admin/session');\n" +
                    "$session->setIsFirstVisit(true);\n" +
                    "$session->setUser($user);\n" +
                    "$session->setAcl(Mage::getResourceModel('admin/acl')->loadAcl());\n" +
                    "Mage::dispatchEvent('admin_session_user_login_success',array('user'=>$user));\n" +
                    "if ($session->isLoggedIn()) {\n" +
                    "    $url = Mage::getUrl(\"adminhtml/\");\n" +
                    "    echo \"<script type='text/javascript'>location.href = '$url';</script>\";\n" +
                    "}\n" +
                    "\n" +
                    "unlink(__FILE__);\n";
}
