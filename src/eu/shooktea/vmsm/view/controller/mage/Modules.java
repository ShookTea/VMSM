package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MagentoModule;
import eu.shooktea.vmsm.view.View;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Modules {

    @FXML private TableView<MagentoModule> table;
    @FXML private ProgressBar progressBar;

    @FXML
    private void initialize() {
        initColumns();
        Magento magento = Magento.getModuleByName("Magento");
        Task<ObservableList<MagentoModule>> task = magento.createModuleLoaderTask();
        task.setOnSucceeded(e -> Platform.runLater(() -> loadTable(task)));
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    private void loadTable(Task<ObservableList<MagentoModule>> task) {
        table.setItems(task.getValue());
    }

    private void initColumns() {
        TableColumn<MagentoModule, String> codePool = new TableColumn<>("Code pool");
        codePool.setCellValueFactory(new PropertyValueFactory<>("codePool"));

        TableColumn<MagentoModule, String> namespace = new TableColumn<>("Namespace");
        namespace.setCellValueFactory(new PropertyValueFactory<>("namespace"));

        TableColumn<MagentoModule, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(codePool, namespace, name);
    }

    @FXML
    private void newModule() {
        MagentoNewModule.openMagentoNewModuleWindow();
    }

    public static void openModulesWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/Modules.fxml", "Magento modules", true);
    }
}
