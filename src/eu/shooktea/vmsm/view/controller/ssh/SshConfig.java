package eu.shooktea.vmsm.view.controller.ssh;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.SSH;
import eu.shooktea.vmsm.view.controller.MainView;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SshConfig implements StageController {
    @FXML private TextField host;
    @FXML private TextField username;
    @FXML private PasswordField password;

    @FXML
    private void initialize() {
        VirtualMachine vm = Start.virtualMachineProperty.get();
        SSH ssh = (SSH)SSH.getModuleByName("SSH");
        String hostAddress = ssh.getStringSetting(vm, "host");
        String userName = ssh.getStringSetting(vm, "user");
        String password = ssh.getStringSetting(vm, "password");
        if (hostAddress == null) hostAddress = vm.getPageRoot().getHost();

        this.host.setText(hostAddress);
        this.username.setText(userName == null ? "" : userName);
        this.password.setText(password == null ? "" : password);
    }

    @FXML
    private void save() {
        VirtualMachine vm = Start.virtualMachineProperty.get();
        SSH ssh = (SSH)SSH.getModuleByName("SSH");
        ssh.setSetting(vm, "host", host.getText());
        ssh.setSetting(vm, "user", username.getText());
        ssh.setSetting(vm, "password", password.getText());
        Storage.saveAll();
        stage.close();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;

    public static void openSshConfigWindow(Object... lambdaArgs) {
        MainView.createNewWindow("/eu/shooktea/vmsm/view/fxml/ssh/SshConfig.fxml", "SSH Configuration", true);
    }
}
