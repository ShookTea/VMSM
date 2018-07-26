package eu.shooktea.vmsm.module;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.dom.*;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.mage.MagentoConfig;
import eu.shooktea.vmsm.view.controller.mage.MagentoNewModule;
import eu.shooktea.vmsm.view.controller.mage.MagentoReportsList;
import eu.shooktea.vmsm.view.controller.MainWindow;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import org.reactfx.value.Val;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
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
    public void afterModuleInstalled() {
        super.afterModuleInstalled();
        if (!View.controller().menuBar.getMenus().contains(magentoMenu)) {
            View.controller().menuBar.getMenus().add(magentoMenu);
        }
    }

    @Override
    public void afterModuleRemoved() {
        super.afterModuleRemoved();
        View.controller().menuBar.getMenus().remove(magentoMenu);
        View.controller().toolBar.getItems().removeAll(toolbarElements);
    }

    @Override
    public void afterModuleLoaded() {
        super.afterModuleLoaded();
        Platform.runLater(() -> {
            if (!View.controller().menuBar.getMenus().contains(magentoMenu)) {
                View.controller().menuBar.getMenus().add(magentoMenu);
            }
        });
    }

    @Override
    public void afterModuleTurnedOff() {
        super.afterModuleTurnedOff();
        Platform.runLater(() -> {
            View.controller().menuBar.getMenus().remove(magentoMenu);
            View.controller().toolBar.getItems().removeAll(toolbarElements);
        });
    }

    @Override
    public void reloadToolbar() {
        View.controller().toolBar.getItems().addAll(toolbarElements);
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

    private static final Menu magentoMenu = createMenu();
    private static final List<Node> toolbarElements = createToolbar();

    private static Menu createMenu() {
        MenuItem deleteCache = new MenuItem("Delete cache files", Toolkit.createMenuImage("trash_full.png"));
        deleteCache.setAccelerator(KeyCombination.valueOf("Ctrl+D"));
        deleteCache.setOnAction(e -> deleteAllInVar("cache"));

        MenuItem deleteCache2 = new MenuItem("Cache files");
        deleteCache2.setOnAction(e -> deleteAllInVar("cache"));

        MenuItem deleteLogs = new MenuItem("Logs");
        deleteLogs.setOnAction(e -> deleteAllInVar("log"));

        MenuItem deleteReports = new MenuItem("Exception reports");
        deleteReports.setOnAction(e -> deleteAllInVar("report"));

        MenuItem deleteSession = new MenuItem("User session");
        deleteSession.setOnAction(e -> deleteAllInVar("session"));

        MenuItem deleteAll = new MenuItem("All");
        deleteAll.setOnAction(e -> deleteAllInVar("cache", "log", "report", "session"));

        Menu removeSubmenu = new Menu("Delete", Toolkit.createMenuImage("trash_full.png"),
                deleteAll, new SeparatorMenuItem(), deleteCache2, deleteLogs, deleteReports, deleteSession);

        MenuItem loginAsAdmin = new MenuItem("Login to admin panel", Toolkit.createMenuImage("user.png"));
        loginAsAdmin.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+A"));
        loginAsAdmin.setOnAction(e -> loginAsAdmin());

        MenuItem newMagentoModule = new MenuItem("Create new module...");
        newMagentoModule.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+N"));
        newMagentoModule.setOnAction(MagentoNewModule::openMagentoNewModuleWindow);

        MenuItem reportsList = new MenuItem("Exception reports...");
        reportsList.setOnAction(MagentoReportsList::openMagentoReportsList);

        return new Menu("Magento", Toolkit.createMenuImage("magento.png"),
                deleteCache, removeSubmenu, loginAsAdmin,
                new SeparatorMenuItem(),
                newMagentoModule, reportsList
        );
    }

    /**
     * Removes all files in {@code /var/X} directories, where X are loaded from arguments.
     * @param varSubdirs subdirectories in {@code /var} directory that should be cleaned.
     */
    public static void deleteAllInVar(String... varSubdirs) {
        VirtualMachine current = VM.getOrThrow();
        String mainPath = getModuleByName("Magento").getStringSetting(current, "path");
        if (mainPath == null) return;

        File root = new File(mainPath);
        if (!root.exists() || !root.isDirectory()) return;

        File var = new File(root, "var");
        if (!var.exists() || !var.isDirectory()) return;

        for (String subdir : varSubdirs) {
            File toRemoveDir = new File(var, subdir);
            if (toRemoveDir.exists() && toRemoveDir.isDirectory()) {
                for (File file : toRemoveDir.listFiles()) {
                    delete(file);
                }
            }
        }
    }

    private static void delete(File file) {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) delete(c);
        }
        if (!file.delete()) file.deleteOnExit();
    }

    private static List<Node> createToolbar() {
        ImageView removeCache = Toolkit.createToolbarImage("trash_full.png");
        Tooltip removeCacheTip = new Tooltip("Delete cache");
        Tooltip.install(removeCache, removeCacheTip);
        removeCache.setOnMouseClicked(e -> deleteAllInVar("cache"));

        ImageView loginAsAdmin = Toolkit.createToolbarImage("user.png");
        Tooltip loginAsAdminTip = new Tooltip("Login to admin panel");
        Tooltip.install(loginAsAdmin, loginAsAdminTip);
        loginAsAdmin.setOnMouseClicked(e -> loginAsAdmin());

        ImageView reportsInfo = Toolkit.createToolbarImage("reports/0.png");
        Tooltip reportsInfoTip = new Tooltip("There are no new exception reports");
        Tooltip.install(reportsInfo, reportsInfoTip);
        reportsInfo.imageProperty().bind(
                Val.map(MagentoReport.newReportsCount, number -> { if (((Integer)number) > 3) return 3; else return (Integer)number; })
                .map(number -> { if (number == 3) return "3plus.png"; else return number + ".png"; })
                .map(fileString -> "/eu/shooktea/vmsm/resources/reports/" + fileString)
                .map(path -> new Image(Magento.class.getResourceAsStream(path)))
        );
        reportsInfoTip.textProperty().bind(Val.map(MagentoReport.newReportsCount, number -> (Integer)number )
                .map(number -> {
                    if (number == 0) return "There are no new exception reports";
                    else if (number == 1) return "There is one new exception report";
                    else return "There are " + number + " new exception reports";
                })
        );
        reportsInfo.setOnMouseClicked(MagentoReportsList::openMagentoReportsList);
        reportsInfo.setPickOnBounds(true);

        return Arrays.asList(
                new Separator(Orientation.VERTICAL),
                Toolkit.createToolbarImage("magento.png"),
                removeCache,
                loginAsAdmin,
                reportsInfo
        );
    }

    @Override
    public int toolbarOrder() {
        return -5;
    }

    private static void loginAsAdmin() {
        VirtualMachine vm = VM.getOrThrow();
        Magento magento = Module.getModuleByName("Magento");
        String address = magento.getAdminAddress(vm);
        MainWindow mw = View.controller();
        String currentAddress = vm.getPageRoot().toString();
        if (!currentAddress.endsWith("/")) currentAddress = currentAddress + "/";
        currentAddress = currentAddress + address;

        String login = magento.getStringSetting(vm, "adm_login");
        String pass = magento.getStringSetting(vm, "adm_pass");

        Browser browser = mw.browser;

        LoadListener listener = new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                super.onFinishLoadingFrame(event);
                if (event.isMainFrame()) {
                    browser.removeLoadListener(this);
                    DOMDocument document = browser.getDocument();
                    DOMFormElement form = (DOMFormElement)document.findElement(By.id("loginForm"));
                    if (form != null) {
                        if (login != null)
                            document.findElement(By.name("login[username]")).setAttribute("value", login);
                        if( pass != null)
                            document.findElement(By.name("login[password]")).setAttribute("value", pass);
                        if (login != null && pass != null)
                            form.submit();
                    }
                }
            }
        };

        browser.addLoadListener(listener);
        browser.loadURL(currentAddress);
    }
}
