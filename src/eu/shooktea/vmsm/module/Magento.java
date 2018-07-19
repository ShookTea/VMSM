package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.controller.MagentoConfig;
import eu.shooktea.vmsm.view.controller.MainWindow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Magento extends Module {
    public Magento() {
        super();
    }

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
        if (!Start.mainWindow.menuBar.getMenus().contains(magentoMenu)) {
            Start.mainWindow.menuBar.getMenus().add(magentoMenu);
        }
    }

    @Override
    public void afterModuleRemoved() {
        super.afterModuleRemoved();
        Start.mainWindow.menuBar.getMenus().remove(magentoMenu);
        Start.mainWindow.toolBar.getItems().removeAll(toolbarElements);
    }

    @Override
    public void afterModuleLoaded() {
        super.afterModuleLoaded();
        Platform.runLater(() -> {
            if (!Start.mainWindow.menuBar.getMenus().contains(magentoMenu)) {
                Start.mainWindow.menuBar.getMenus().add(magentoMenu);
            }
        });
    }

    @Override
    public void afterModuleTurnedOff() {
        super.afterModuleTurnedOff();
        Platform.runLater(() -> {
            Start.mainWindow.menuBar.getMenus().remove(magentoMenu);
            Start.mainWindow.toolBar.getItems().removeAll(toolbarElements);
        });
    }

    @Override
    public void reloadToolbar() {
        Start.mainWindow.toolBar.getItems().addAll(toolbarElements);
    }

    public String getAdminAddress(VirtualMachine vm) {
        try {
            String rootPath = getSetting(vm, "path");
            if (rootPath == null) throw new IOException("rootPath == null");
            File rootFile = new File(rootPath);
            if (!rootFile.exists()) throw new IOException("rootFile " + rootFile.toString() + "doesn't exist");
            File localXmlFile = new File(rootFile, "app/etc/local.xml");
            if (!localXmlFile.exists()) throw new IOException("local xml file " + localXmlFile.toString() + " doesn't exist");
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(localXmlFile);

            Element config = doc.getDocumentElement();
            config.normalize();
            Element admin = (Element)config.getElementsByTagName("admin").item(0);
            Element routers = (Element)admin.getElementsByTagName("routers").item(0);
            Element adminhtml = (Element)routers.getElementsByTagName("adminhtml").item(0);
            Element args = (Element)adminhtml.getElementsByTagName("args").item(0);
            Element frontname = (Element)args.getElementsByTagName("frontName").item(0);
            return frontname.getTextContent().trim();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
            return "admin";
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return "admin";
        }
    }

    private static final Menu magentoMenu = createMenu();
    private static final List<Node> toolbarElements = createToolbar();

    private static Menu createMenu() {
        MenuItem deleteCache = new MenuItem("Delete cache files", Start.createMenuImage("trash_full.png"));
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

        Menu removeSubmenu = new Menu("Delete...", Start.createMenuImage("trash_full.png"),
                deleteAll, new SeparatorMenuItem(), deleteCache2, deleteLogs, deleteReports, deleteSession);

        MenuItem loginAsAdmin = new MenuItem("Login as administrator");
        loginAsAdmin.setOnAction(e -> loginAsAdmin());

        return new Menu("Magento", Start.createMenuImage("magento.png"),
                deleteCache, removeSubmenu, loginAsAdmin
        );
    }

    private static void deleteAllInVar(String... varSubdirs) {
        VirtualMachine current = Start.virtualMachineProperty.get();
        String mainPath = getModuleByName("Magento").getSetting(current, "path");
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
        ImageView removeCache = Start.createToolbarImage("trash_full.png");
        Tooltip removeCacheTip = new Tooltip("Delete cache");
        Tooltip.install(removeCache, removeCacheTip);
        removeCache.setOnMouseClicked(e -> deleteAllInVar("cache"));

        return Arrays.asList(
                new Separator(Orientation.VERTICAL),
                Start.createToolbarImage("magento.png"),
                removeCache
        );
    }

    private static void loginAsAdmin() {
        VirtualMachine vm = Start.virtualMachineProperty.get();
        Magento magento = (Magento)Module.getModuleByName("Magento");
        String address = magento.getAdminAddress(vm);
        if (address == null) return;
        MainWindow mw = Start.mainWindow;
        String currentAddress = vm.getPageRoot().toString();
        if (!currentAddress.endsWith("/")) currentAddress = currentAddress + "/";
        currentAddress = currentAddress + address;

        WebEngine engine = mw.webEngine;
        final ChangeListener<Document> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document doc) {
                engine.documentProperty().removeListener(this);
                String login = magento.getSetting(vm, "adm_login");
                String pass = magento.getSetting(vm, "adm_pass");

                if (login != null) engine.executeScript("document.getElementsByName('login[username]')[0].value = '" + login + "';");
                if (pass != null) engine.executeScript("document.getElementsByName('login[password]')[0].value = '" + pass + "';");
                if (login != null && pass != null) engine.executeScript("document.getElementById('loginForm').submit();");
            }
        };
        engine.documentProperty().addListener(listener);

        mw.goTo(currentAddress);
    }
}
