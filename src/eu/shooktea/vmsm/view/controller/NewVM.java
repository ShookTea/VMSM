/*
MIT License

Copyright (c) 2018 Norbert Kowalik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class NewVM implements StageController {
    @FXML private TextField vmName;
    @FXML private ChoiceBox<VMType> vmType;
    @FXML private TextField vmPath;
    @FXML private TextField vmAddress;
    @FXML private Label infoLabel;
    @FXML private Label errorLabel;
    @FXML private Label headerLabel;
    private Stage stage;

    @FXML
    private void initialize() {
        if (machineToEdit != null) {
            headerLabel.setText("Editing \"" + machineToEdit.getName() + "\" VM");
        }
        vmType.setItems(VMType.types);
        vmType.getSelectionModel().selectFirst();
        vmPath.setPromptText(System.getProperty("user.home"));

        infoLabel.textProperty().bind(Bindings.select(vmType.valueProperty(), "creationInfo"));
        infoLabel.getParent().opacityProperty().bind(
                new When(infoLabel.textProperty().isEmpty())
                        .then(0.0)
                        .otherwise(1.0));
        errorLabel.getParent().opacityProperty().bind(
                new When(errorLabel.textProperty().isEmpty())
                        .then(0.0)
                        .otherwise(1.0));
    }

    @FXML
    private void openPathWindow() {
        File file;
        String dialogTitle = "Choose VM path";
        if (vmType.getValue().isMainPathDirectory()) {
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle(dialogTitle);
            file = fileChooser.showDialog(Start.primaryStage);
        }
        else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(dialogTitle);
            file = fileChooser.showOpenDialog(Start.primaryStage);
        }
        if (file != null) vmPath.setText(file.getAbsolutePath());
        vmType.getValue().checkVmRootFile(file);
        errorLabel.setText(vmType.getValue().getCreationError());
    }

    @FXML
    private void create() {
        File rootFile = new File(vmPath.getText());
        VMType type = vmType.getValue();
        type.checkVmRootFile(rootFile);
        errorLabel.setText(type.getCreationError());
        if (!type.getCreationError().isEmpty()) {
            return;
        }
        if (vmName.getText().trim().isEmpty()) {
            errorLabel.setText(VM_NAME_IS_REQUIRED);
            return;
        }
        String name = vmName.getText().trim();
        String urlText = vmAddress.getText().trim();
        if (!urlText.startsWith("http://") && !urlText.startsWith("https://")) {
            urlText = "http://" + urlText;
        }
        URL url = null;
        if (!vmAddress.getText().trim().isEmpty()) {
            try {
                url = new URL(urlText);
            } catch (MalformedURLException e) {
                errorLabel.setText(VM_URL_IS_INCORRECT);
                return;
            }
        }
        File file = new File(vmPath.getText().trim());
        VirtualMachine machine = new VirtualMachine(name, file, url, vmType.getValue());
        Storage.registerVM(machine);
        stage.close();
        Start.virtualMachineProperty.setValue(machine);
    }

    @FXML
    private void onNameInput() {
        if (errorLabel.getText().equals(VM_NAME_IS_REQUIRED)) {
            errorLabel.setText("");
        }
    }

    @FXML
    private void cancel() {
        stage.close();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private final String VM_NAME_IS_REQUIRED = "VM name is required.";
    private final String VM_URL_IS_INCORRECT = "VM address is incorrect.";

    private static VirtualMachine machineToEdit = null;

    public static void openNewVmWindow(Object... lambdaArgs) {
        machineToEdit = null;
        if (lambdaArgs.length > 0 && lambdaArgs[0] instanceof VirtualMachine) {
            machineToEdit = (VirtualMachine)lambdaArgs[0];
        }
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/NewVM.fxml", "New VM", true);
    }
}
