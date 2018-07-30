package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MagentoModule;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        TableColumn<MagentoModule, String> codePool = new TableColumn<>("Code pool");
        codePool.setCellValueFactory(new PropertyValueFactory<>("codePool"));
        codePool.prefWidthProperty().bind(table.widthProperty().divide(100).multiply(20));

        TableColumn<MagentoModule, String> namespace = new TableColumn<>("Namespace");
        namespace.setCellValueFactory(new PropertyValueFactory<>("namespace"));
        namespace.prefWidthProperty().bind(table.widthProperty().divide(100).multiply(20));

        TableColumn<MagentoModule, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.prefWidthProperty().bind(table.widthProperty().divide(100).multiply(20));

        TableColumn<MagentoModule, String> installedVersion = new TableColumn<>("Installed version");
        installedVersion.setCellValueFactory(new PropertyValueFactory<>("installedVersion"));
        installedVersion.prefWidthProperty().bind(table.widthProperty().divide(100).multiply(19));

        TableColumn<MagentoModule, String> xmlVersion = new TableColumn<>("XML version");
        xmlVersion.setCellValueFactory(new PropertyValueFactory<>("xmlVersion"));
        xmlVersion.prefWidthProperty().bind(table.widthProperty().divide(100).multiply(19));

        table.getColumns().addAll(codePool, namespace, name, installedVersion, xmlVersion);
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
