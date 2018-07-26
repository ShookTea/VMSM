package eu.shooktea.vmsm.view.controller.mysql;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.MySQL;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MysqlConfig implements StageController {

    @FXML private TextField database;
    @FXML private TextField username;
    @FXML private PasswordField password;

    private VirtualMachine vm;
    private MySQL mysql;
    private Stage stage;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        mysql = MySQL.getModuleByName("MySQL");

        String database = mysql.getStringSetting(vm, "database");
        String username = mysql.getStringSetting(vm, "username");
        String password = mysql.getStringSetting(vm, "password");

        this.database.setText(database == null ? "" : database);
        this.username.setText(username == null ? "" : username);
        this.password.setText(password == null ? "" : password);
    }

    @FXML
    private void save() {
        mysql.setSetting(vm, "database", database.getText());
        mysql.setSetting(vm, "username", username.getText());
        mysql.setSetting(vm, "password", password.getText());
        Storage.saveAll();
        stage.close();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openMysqlConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/MysqlConfig.fxml", "MySQL config", true);
    }
}
