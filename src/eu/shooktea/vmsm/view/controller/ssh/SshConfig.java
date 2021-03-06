package eu.shooktea.vmsm.view.controller.ssh;

import eu.shooktea.datamodel.DataModelPrimitive;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.ssh.SSH;
import eu.shooktea.vmsm.view.StageController;
import eu.shooktea.vmsm.view.View;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SshConfig implements StageController {
    @FXML private TextField host;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private CheckBox fingerprints;

    private SSH ssh;
    private VirtualMachine vm;

    @FXML
    private void initialize() {
        vm = VM.getOrThrow();
        ssh = SSH.getModuleByName("SSH");
        String hostAddress = ssh.getStringSetting(vm, "host");
        String userName = ssh.getStringSetting(vm, "user");
        String password = ssh.getStringSetting(vm, "password");

        Boolean fingerprints = ssh.getSetting(vm, "auto_fingerprints", new DataModelPrimitive<>(true))
                .<Boolean>toPrimitive().getContent();
        if (hostAddress == null) hostAddress = vm.getPageRoot().getHost();

        this.host.setText(hostAddress);
        this.username.setText(userName == null ? "" : userName);
        this.password.setText(password == null ? "" : password);
        this.fingerprints.setSelected(fingerprints == null ? true : fingerprints);
    }

    @FXML
    private void save() {
        ssh.setSetting(vm, "host", host.getText());
        ssh.setSetting(vm, "user", username.getText());
        ssh.setSetting(vm, "password", password.getText());
        ssh.setSetting(vm, "auto_fingerprints", fingerprints.isSelected());
        Storage.saveAll();
        stage.close();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;

    public static void openSshConfigWindow(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/ssh/SshConfig.fxml", "SSH Configuration");
    }
}
