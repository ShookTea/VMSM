package eu.shooktea.vmsm.view.controller.mage;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Magento;
import eu.shooktea.vmsm.module.MagentoModule;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ModuleInfo implements StageController {
    @FXML private Label codePoolName;
    @FXML private Label moduleName;

    @FXML
    private void initialize() {}

    public void setValue(Magento magento, MagentoModule module, VirtualMachine vm) {
        this.magento = magento;
        this.module = module;
        this.vm = vm;
        reloadData();
    }

    private void reloadData() {
        codePoolName.setText(module.getCodePool());
        moduleName.setText(module.getNamespace() + " " + module.getName());
    }

    @Override
    public void setStage(Stage stage) {

    }

    private Magento magento;
    private MagentoModule module;
    private VirtualMachine vm;

    public static void openModuleInfo(Magento magento, MagentoModule module) {
        String path = "/eu/shooktea/vmsm/view/fxml/mage/ModuleInfo.fxml";
        String title = module.getCodePool() + "/" + module.getNamespace() + "_" + module.getName() + " - module info";
        ModuleInfo contr = View.createNewWindow(path, title, false);
        contr.setValue(magento, module, VM.getOrThrow());
    }
}
