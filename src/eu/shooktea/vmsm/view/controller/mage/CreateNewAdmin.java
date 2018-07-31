package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MySQL;
import eu.shooktea.vmsm.module.SqlConnection;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class CreateNewAdmin implements StageController {
    @FXML private TextField login;
    @FXML private PasswordField password;
    @FXML private TextField salt;
    @FXML private TextField name;
    @FXML private TextField surname;
    @FXML private TextField email;
    @FXML private Label error;
    private Stage stage;

    @FXML
    private void create() {
        if (login.getText().trim().isEmpty() || password.getText().isEmpty()) {
            error.setText("Login and password are required values.");
            return;
        }
        error.setText("");

        String login = this.login.getText().trim();
        String password = this.password.getText();
        String salt = this.salt.getText().trim();
        String name = this.name.getText().trim();
        String surname = this.surname.getText().trim();
        String email = this.email.getText().trim();

        if (salt.isEmpty()) salt = "salt";
        if (name.isEmpty()) name = "John";
        if (surname.isEmpty()) surname = "Smith";
        if (email.isEmpty()) email = "johnsmith@example.tld";

        MySQL sql = MySQL.getModuleByName("MySQL");
        VirtualMachine vm = VM.getOrThrow();
        connection = sql.createConnection();
        try {
            connection.open();
            query("LOCK TABLES `admin_role` WRITE, `admin_user` WRITE");
            query("SET @SALT = \"" + salt + "\"");
            query("SET @PASS = CONCAT(MD5(CONCAT(@SALT, \"" + password + "\")), CONCAT(\":\", @SALT))");
            query("SELECT @EXTRA := MAX(extra) FROM admin_user WHERE extra IS NOT NULL");
            query("INSERT INTO `admin_user` (firstname, lastname, email, username, password, created, lognum, reload_acl_flag, is_active, extra, rp_token_created_at) " +
                    "VALUES ('" + name + "','" + surname + "','" + email + "','" + login + "',@PASS,NOW(),0,0,1,@EXTRA,NOW())");
            query("INSERT INTO `admin_role` (parent_id, tree_level, sort_order, role_type, user_id, role_name) " +
                    "VALUES(1,2,0,'U',(SELECT user_id FROM admin_user WHERE username = '" + login + "'),'" + name + "')");
            query("UNLOCK TABLES");
            connection.close();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Admin created");
            alert.setHeaderText("Admin account created");
            alert.setContentText("Do you want to set it as default account?");
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yes, no);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == yes) {
                Magento magento = Magento.getModuleByName("Magento");
                magento.setSetting(vm, "adm_login", login);
                magento.setSetting(vm, "adm_pass", password);
                Storage.saveAll();
            }
            stage.close();
        } catch (Exception ex) {
            error.setText("Exception: " + ex.getMessage());
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    SqlConnection connection;

    private void query(String query) throws SQLException {
        Object ob = connection.query(query);
        if (ob instanceof ResultSet) {
            ResultSet result = (ResultSet)ob;
            result.close();
        }
    }

    public static void openAdminCreationWindow(Object... lambdaArgs) {
        MySQL sql = MySQL.getModuleByName("MySQL");
        VirtualMachine vm = VM.getOrThrow();
        if (!sql.isInstalled(vm)) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setContentText("You need to configure MySQL module first.");
            dialog.showAndWait();
        }
        else {
            View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mage/CreateNewAdmin.fxml", "Create new admin", true);
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
