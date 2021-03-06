package eu.shooktea.vmsm.view.controller.mysql;

import eu.shooktea.datamodel.DataModelMap;
import eu.shooktea.datamodel.DataModelPrimitive;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.mysql.MySQL;
import eu.shooktea.vmsm.module.ssh.SSH;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.StageController;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MysqlConfig implements StageController {

    @FXML private TextField database;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private TextField host;
    @FXML private TextField port;

    @FXML private CheckBox enableSsh;
    @FXML private TextField sshHost;
    @FXML private TextField sshPort;
    @FXML private TextField sshUsername;
    @FXML private PasswordField sshPassword;
    @FXML private TextField localPort;

    private VirtualMachine vm;
    private MySQL mysql;
    private SSH ssh;
    private Stage stage;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        mysql = MySQL.getModuleByName("MySQL");
        ssh = SSH.getModuleByName("SSH");
        bindSsh();
        loadMysqlSettings();
        Boolean sshEnabled = mysql.getSetting(vm, "ssh_enabled", new DataModelPrimitive<>(false))
                .<Boolean>toPrimitive().getContent();
        if (sshEnabled == null) sshEnabled = false;
        enableSsh.setSelected(sshEnabled);
        loadSshSettings();
    }

    private void bindSsh() {
        BooleanBinding not = enableSsh.selectedProperty().not();
        sshHost.disableProperty().bind(not);
        sshPort.disableProperty().bind(not);
        sshUsername.disableProperty().bind(not);
        sshPassword.disableProperty().bind(not);
        localPort.disableProperty().bind(not);
    }

    private void loadMysqlSettings() {
        String database = mysql.getStringSetting(vm, "database");
        String username = mysql.getStringSetting(vm, "username");
        String password = mysql.getStringSetting(vm, "password");
        String host = mysql.getStringSetting(vm, "host");
        String port = mysql.getStringSetting(vm, "port");

        this.database.setText(database == null ? "" : database);
        this.username.setText(username == null ? "" : username);
        this.password.setText(password == null ? "" : password);
        this.host.setText(host == null ? "127.0.0.1" : host);
        this.port.setText(port == null ? "3306" : port);
    }

    private void loadSshSettings() {
        DataModelMap sshConfig = (DataModelMap)mysql.getSetting(vm, "ssh");
        if (sshConfig == null) sshConfig = new DataModelMap();
        Map<String, String> defaults = new HashMap<>();
        defaults.put("host", vm.getPageRoot().getHost());
        defaults.put("port", "22");
        defaults.put("local_port", "3307");

        if (ssh.isInstalled(vm)) {
            String defHost = ssh.getStringSetting(vm, "host");
            if (defHost != null && !defHost.trim().isEmpty()) defaults.put("host", defHost);

            String user = ssh.getStringSetting(vm, "user");
            if (user != null && !user.trim().isEmpty()) defaults.put("username", user);

            String pass = ssh.getStringSetting(vm, "password");
            if (pass != null && !pass.trim().isEmpty()) defaults.put("password", pass);
        }

        sshHost.setText(load(sshConfig, "host", defaults));
        sshPort.setText(load(sshConfig, "port", defaults));
        sshUsername.setText(load(sshConfig, "username", defaults));
        sshPassword.setText(load(sshConfig, "password", defaults));
        localPort.setText(load(sshConfig, "local_port", defaults));

    }

    private String load(DataModelMap obj, String key, Map<String, String> def) {
        String base = obj.containsKey(key) ? obj.getString(key).trim() : "";
        if (base.isEmpty()) base = def.getOrDefault(key, "");
        return base;
    }

    @FXML
    private void save() {
        mysql.setSetting(vm, "database", database.getText());
        mysql.setSetting(vm, "username", username.getText());
        mysql.setSetting(vm, "password", password.getText());
        mysql.setSetting(vm, "host", host.getText());
        mysql.setSetting(vm, "port", port.getText());
        mysql.setSetting(vm, "ssh_enabled", enableSsh.isSelected());

        DataModelMap ssh = new DataModelMap();
        ssh.put("host", sshHost.getText());
        ssh.put("port", sshPort.getText());
        ssh.put("username", sshUsername.getText());
        ssh.put("password", sshPassword.getText());
        ssh.put("local_port", localPort.getText());
        mysql.setSetting(vm, "ssh", ssh);

        Storage.saveAll();
        stage.close();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public static void openMysqlConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/MysqlConfig.fxml", "MySQL config");
    }
}
