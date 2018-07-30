package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MagentoModule;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Modules implements StageController {

    @FXML private TableView<MagentoModule> table;
    @FXML private ProgressBar progressBar;
    private Task<ObservableList<MagentoModule>> task;

    @FXML
    private void initialize() {
        initColumns();
        Magento magento = Magento.getModuleByName("Magento");
        task = magento.createModuleLoaderTask();
        task.setOnSucceeded(e -> Platform.runLater(() -> loadTable(task)));
        progressBar.progressProperty().bind(task.progressProperty());
    }

    private void loadTable(Task<ObservableList<MagentoModule>> task) {
        table.setItems(task.getValue());
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
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/Modules.fxml", "Magento modules", true);
    }

    @Override
    public void setStage(Stage stage) {
        new Thread(task).start();
    }
}
