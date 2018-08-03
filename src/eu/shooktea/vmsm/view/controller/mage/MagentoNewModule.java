package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.PrintWriter;


public class MagentoNewModule implements StageController {

    @FXML private TextField namespaceField;
    @FXML private TextField nameField;
    @FXML private TextField fullModuleNameField;
    @FXML private TextField versionField;
    @FXML private ChoiceBox<String> codePoolField;
    @FXML private CheckBox model;
    @FXML private CheckBox installer;
    @FXML private CheckBox helper;
    @FXML private CheckBox block;
    @FXML private CheckBox removeCache;
    @FXML private CheckBox activateModule;

    private Stage stage;

    private VirtualMachine vm;
    private Magento magento;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        magento = Magento.getModuleByName("Magento");

        namespaceField.setOnKeyTyped(e -> updateTextFields(false));
        nameField.setOnKeyTyped(e -> updateTextFields(false));
        fullModuleNameField.setOnKeyTyped(e -> updateTextFields(true));
        codePoolField.setItems(FXCollections.observableArrayList("core", "community", "local"));
        codePoolField.setValue("local");
    }

    private void updateTextFields(boolean fullNameUpdated) {
        if (fullNameUpdated) {
            String fullName = fullModuleNameField.getText();
            if (fullName.contains("_")) {
                String[] parts = fullName.split("_");
                namespaceField.setText(parts[0].trim());
                if (parts.length > 1) nameField.setText(parts[1].trim());
            }
            else {
                namespaceField.setText("Mage");
                nameField.setText(fullName);
            }
        }
        else {
            String namespace = namespaceField.getText().trim();
            if (namespace.isEmpty()) namespace = "Mage";
            String modName = nameField.getText().trim();
            fullModuleNameField.setText(namespace + "_" + modName);
        }
    }

    @FXML
    private void createAction() {
        String path = magento.getStringSetting(vm, "path");
        if (path == null) {
            showError("You haven't configured Magento main directory!");
            return;
        }
        File root = new File(path);
        File codeRoot = new File(root, "app/code");
        File moduleDeclarationRoot = new File(root, "app/etc/modules");

        String namespace = namespaceField.getText().trim();
        String moduleName = nameField.getText().trim();
        if (namespace.isEmpty()) namespace = "Mage";
        if (moduleName.isEmpty()) {
            showError("Module name cannot be empty!");
            return;
        }
        String fullModuleName = namespace + "_" + moduleName;
        String codePool = codePoolField.getValue();

        String version = versionField.getText().trim();
        if (version.isEmpty()) version = "0.1.0";

        File moduleRoot = new File(codeRoot, codePool + "/" + namespace + "/" + moduleName);
        if (moduleRoot.exists()) {
            showError("Module " + fullModuleName + " exists in " + codePool + " code pool already.");
            return;
        }
        moduleRoot.mkdirs();

        try {
            createModuleDeclaration(moduleDeclarationRoot, fullModuleName);
            createModuleConfigFile(moduleRoot, fullModuleName, version);
            if (removeCache.isSelected()) {
                magento.deleteAllInVar(vm, "cache");
            }
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("IO Exception happened :(");
        }
    }

    private void createModuleDeclaration(File root, String moduleName) throws Exception {
        File entry = new File(root, moduleName + ".xml");
        entry.createNewFile();
        doc = createNewDocument();

        Element config = doc.createElement("config");
        doc.appendChild(config);

        Element module = createChild(moduleName, config);
        Element active = createChild("active", module);
        Element pool = createChild("codePool", module);

        active.setTextContent(activateModule.isSelected() ? "true" : "false");
        pool.setTextContent(codePoolField.getValue());

        saveDocument(entry, doc);
    }

    private void createModuleConfigFile(File moduleRoot, String moduleName, String versionText) throws Exception {
        File configFile = new File(moduleRoot, "etc/config.xml");
        configFile.getParentFile().mkdirs();
        configFile.createNewFile();
        doc = createNewDocument();

        Element config = doc.createElement("config");
        doc.appendChild(config);

        Element modules = createChild("modules", config);
        Element modulesM = createChild(moduleName, modules);
        Element version = createChild("version", modulesM);

        Element global = createChild("global", config);

        if (helper.isSelected()) {
            Element helpers = createChild("helpers", global);
            Element helperM = createChild(moduleName.toLowerCase(), helpers);
            Element clazz = createChild("class", helperM);
            clazz.setTextContent(moduleName + "_Helper");
            createHelper(moduleRoot, moduleName);
        }

        if (installer.isSelected()) {
            Element resources = createChild("resources", global);
            Element mSetup = createChild(moduleName.toLowerCase() + "_setup", resources);
            Element setup = createChild("setup", mSetup);
            Element module = createChild("module", setup);
            module.setTextContent(moduleName);
            createInstaller(moduleRoot, moduleName, versionText);
        }

        if (block.isSelected()) {
            Element block = createChild("blocks", global);
            Element mBlock = createChild(moduleName.toLowerCase(), block);
            Element clazz = createChild("class", mBlock);
            clazz.setTextContent(moduleName + "_Block");
            new File(moduleRoot, "Block").mkdirs();
        }

        if (model.isSelected()) {
            Element model = createChild("models", global);
            Element mModel = createChild(moduleName.toLowerCase(), model);
            Element clazz = createChild("class", mModel);
            clazz.setTextContent(moduleName + "_Model");
            Element resourceModel = createChild("resourceModel", mModel);
            resourceModel.setTextContent(moduleName.toLowerCase() + "_resource");
            new File(moduleRoot, "Model/Resource").mkdirs();
        }

        version.setTextContent(versionText);

        saveDocument(configFile, doc);
    }

    private Document doc;

    private Element createChild(String tagName, Element parent) {
        Element elem = doc.createElement(tagName);
        parent.appendChild(elem);
        return elem;
    }

    private Document createNewDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    private void saveDocument(File file, Document document) throws Exception {
        Transformer transformer = createTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private Transformer createTransformer() throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return transformer;
    }

    private void createHelper(File moduleRoot, String moduleName) throws Exception {
        File helper = new File(moduleRoot, "Helper/Data.php");
        helper.getParentFile().mkdirs();
        helper.createNewFile();

        PrintWriter writer = new PrintWriter(helper);
        writer.println("<?php");
        writer.println("");
        writer.println("class " + moduleName + "_Helper_Data extends Mage_Core_Helper_Abstract");
        writer.println("{");
        writer.println("\t");
        writer.println("}");
        writer.close();
    }

    private void createInstaller(File moduleRoot, String moduleName, String version) throws Exception {
        File installer = new File(moduleRoot, "sql/" + moduleName.toLowerCase() + "_setup/install-" + version + ".php");
        installer.getParentFile().mkdirs();
        installer.createNewFile();

        PrintWriter writer = new PrintWriter(installer);
        writer.println("<?php");
        writer.println("$installer = $this;");
        writer.println("$installer->startSetup();");
        writer.println("");
        writer.println("$installer->endSetup();");
        writer.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Module error");
        alert.setHeaderText("Module creation error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openMagentoNewModuleWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/MagentoNewModule.fxml", "New module");
    }
}
