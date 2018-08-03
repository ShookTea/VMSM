package eu.shooktea.vmsm.view.controller.mage;

import com.jcraft.jsch.JSchException;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MagentoModule;
import eu.shooktea.vmsm.module.MySQL;
import eu.shooktea.vmsm.module.SqlConnection;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.reactfx.value.Val;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModuleInfo {
    @FXML private Label codePoolName;
    @FXML private Label moduleName;
    @FXML private Label rootDir;
    @FXML private Button revertVersionButton;
    @FXML private ListView<String> versionsList;

    private MagentoModule module;
    private VirtualMachine vm;
    private MySQL sql;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        sql = MySQL.getModuleByName("MySQL");
    }

    public void setValue() {
        versionsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        revertVersionButton.disableProperty().bind(
                Val.flatMap(versionsList.selectionModelProperty(), SelectionModel::selectedItemProperty)
                .map(version -> version.contains("installed"))
                .orElseConst(Boolean.TRUE)
        );
        reloadData();
    }

    private void reloadData() {
        codePoolName.setText(module.getCodePool());
        moduleName.setText(module.getNamespace() + " " + module.getName());
        rootDir.setText(module.getDisplayRootFile());
        createVersionsList();
    }

    private void createVersionsList() {
        versionsList.getItems().setAll(module.createVersionList());
    }

    @FXML
    private void openRootDir() {
        module.openRootDir();
    }

    @FXML
    private void revertVersion() {
        String version = versionsList.getSelectionModel().getSelectedItem();
        version = version.replace("^([^\\(\\)]*)(\\s*\\([^\\)]*\\))?$", "$1");
        SqlConnection conn = sql.createConnection();
        String moduleConfigName = module.getConfigName();
        String query = String.format(
                "UPDATE `core_resource` SET `version`='%s', `data_version`='%s' WHERE `code`='%s'",
                version, version, moduleConfigName);
        try {
            conn.open();
            Object ob = conn.query(query);
            if (ob instanceof ResultSet) {
                ((ResultSet)ob).close();
            }
            conn.close();
            module.setInstalledVersion(version);
            createVersionsList();
        } catch (JSchException | SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You haven't properly configured MySQL module or your Virtual Machine is turned off.");
        }
    }

    public static void openModuleInfo(MagentoModule module) {
        String path = "/eu/shooktea/vmsm/view/fxml/mage/ModuleInfo.fxml";
        String title = module.getCodePool() + "/" + module.getNamespace() + "_" + module.getName() + " - module info";
        ModuleInfo contr = View.createNewWindow(path, title, false);
        contr.module = module;
        contr.setValue();
    }
}
