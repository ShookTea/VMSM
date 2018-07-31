package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.MySQL;
import eu.shooktea.vmsm.module.SqlConnection;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class CreateNewAdmin {
    @FXML private TextField login;
    @FXML private PasswordField password;
    @FXML private TextField salt;
    @FXML private TextField name;
    @FXML private TextField surname;
    @FXML private TextField email;
    @FXML private Label error;


    @FXML
    private void create() {
        if (login.getText().trim().isEmpty() || password.getText().isEmpty()) {
            error.setText("Login and password are required values.");
            return;
        }
        error.setText("");

        String login = this.login.getText();
        String password = this.password.getText();
        String salt = this.salt.getText();
        String name = this.name.getText();
        String surname = this.surname.getText();

        MySQL sql = MySQL.getModuleByName("MySQL");
        VirtualMachine vm = VM.getOrThrow();
        SqlConnection connection = sql.createConnection();
        try {
            connection.open();

            connection.close();
        } catch (Exception ex) {
            error.setText("Exception: " + ex.getMessage());
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
}
