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

public class NewVM implements StageController {
    @FXML private TextField vmName;
    @FXML private ChoiceBox<VMType> vmType;
    @FXML private TextField vmPath;
    @FXML private TextField vmAddress;
    @FXML private Label infoLabel;
    @FXML private Label errorLabel;
    private Stage stage;

    @FXML
    private void initialize() {
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
}
