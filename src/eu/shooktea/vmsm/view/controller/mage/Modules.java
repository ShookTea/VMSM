package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.module.mage.Magento;
import eu.shooktea.vmsm.module.mage.MagentoModule;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.StageController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class Modules implements StageController {

    @FXML private TableView<MagentoModule> table;
    @FXML private ProgressBar progressBar;
    @FXML private TextField codePool;
    @FXML private TextField namespace;
    @FXML private TextField name;
    @FXML private TextField installedVersion;
    @FXML private TextField xmlVersion;
    @FXML private TitledPane filtersPane;
    private Task<ObservableList<MagentoModule>> task;
    private ObservableList<MagentoModule> allModules;

    private Magento magento;

    @FXML
    private void initialize() {
        magento = Magento.getModuleByName("Magento");
        initColumns();
        task = magento.createModuleLoaderTask();
        task.setOnSucceeded(e -> Platform.runLater(() -> {loadTable(task); filtersPane.setExpanded(true);}));
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        progressBar.progressProperty().bind(task.progressProperty());
    }

    private void loadTable(Task<ObservableList<MagentoModule>> task) {
        allModules = task.getValue();
        reloadTable();
        table.setItems(allModules);
    }

    @FXML
    private void reloadTable() {
        table.setItems(allModules
                .filtered(module -> codePool.getText().trim().isEmpty()
                        || module.getCodePool().toUpperCase().contains(codePool.getText().trim().toUpperCase()))
                .filtered(module -> namespace.getText().trim().isEmpty()
                        || module.getNamespace().toUpperCase().contains(namespace.getText().trim().toUpperCase()))
                .filtered(module -> name.getText().trim().isEmpty()
                        || module.getName().toUpperCase().contains(name.getText().trim().toUpperCase()))
                .filtered(module -> installedVersion.getText().trim().isEmpty()
                        || module.getInstalledVersion().toUpperCase().contains(installedVersion.getText().trim().toUpperCase()))
                .filtered(module -> xmlVersion.getText().trim().isEmpty()
                        || module.getXmlVersion().toUpperCase().contains(xmlVersion.getText().trim().toUpperCase()))
        );
    }

    private void initColumns() {
        createColumn("Code pool", "codePool");
        createColumn("Namespace", "namespace");
        createColumn("Name", "name");
        createColumn("Installed version", "installedVersion");
        createColumn("XML version", "xmlVersion");
    }

    private void createColumn(String text, String propertyName) {
        TableColumn<MagentoModule, String> column = new TableColumn<>(text);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.prefWidthProperty().bind(table.widthProperty().divide(100).multiply(20));
        table.getColumns().add(column);
    }


    @FXML
    private void newModule() {
        MagentoNewModule.openMagentoNewModuleWindow();
    }

    public static void openModulesWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/Modules.fxml", "Magento modules");
    }

    @Override
    public void setStage(Stage stage) {
        new Thread(task).start();
    }

    @FXML
    private void tableClicked(MouseEvent e) {
        if (e.getClickCount() != 2) return;
        MagentoModule module = table.getSelectionModel().getSelectedItem();
        if (module == null) return;
        ModuleInfo.openModuleInfo(module);

    }
}
