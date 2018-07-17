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
import javafx.beans.binding.When;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.reactfx.value.Val;

import java.net.URL;


public class MainWindow {

    @FXML private WebView webView;
    @FXML private TextField addressField;
    @FXML private ProgressBar progressBar;
    @FXML private ToolBar toolBar;
    @FXML private Menu vmListMenu;
    @FXML private ImageView homeButton;

    private WebEngine webEngine;
    private ToggleGroup chooseVmToggleGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        Start.virtualMachineProperty.addListener(this::reloadGUI);
        webEngine = webView.getEngine();
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> addressField.setText(newValue));
        webEngine.getLoadWorker().exceptionProperty().addListener(
                (observable, oldValue, newValue) -> displayErrorMessage(newValue));
        bindProgressBar();
        bindHomeButton();

        chooseVmToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            RadioMenuItem item = (RadioMenuItem)newValue;
            String name = item.getText();
            VirtualMachine choosenMachine = Storage.vmList.stream()
                    .filter(vm -> vm.getName().equals(name))
                    .findFirst()
                    .get();
            Start.virtualMachineProperty.setValue(choosenMachine);
        }));
    }

    private void displayErrorMessage(Throwable error) {
        if (error == null) return;
        String message = error.getMessage();
        webEngine.loadContent("<b>" + message + "</b>");
    }

    private void bindProgressBar() {
        progressBar.setStyle("-fx-accent: blue;");
        ReadOnlyDoubleProperty progress = webEngine.getLoadWorker().progressProperty();
        progressBar.progressProperty().bind(
                new When(progress.isEqualTo(0))
                .then(-1)
                .otherwise(
                    new When(progress.lessThan(0.0))
                    .then(0)
                    .otherwise(progress))
        );
    }

    private void bindHomeButton() {
        ColorAdjust effect = (ColorAdjust)homeButton.getEffect();
        effect.saturationProperty().bind(
                Val.flatMap(Start.virtualMachineProperty, VirtualMachine::pageRootProperty)
                .map(url -> url == null ? -1.0 : 0.0)
                .orElseConst(-1.0)
        );
    }

    private void reloadGUI() {
        Start.primaryStage.setTitle(
                Start.virtualMachineProperty.getValue() == null ?
                        "VMSM" :
                        "VMSM - " + Start.virtualMachineProperty.getValue().getName());
        reloadMenu();
        reloadToolbar();
    }

    private void reloadMenu() {
        ObservableList<MenuItem> items = vmListMenu.getItems();
        items.clear();
        for (VirtualMachine vm : Storage.vmList) {
            RadioMenuItem item = new RadioMenuItem(vm.getName());
            item.setToggleGroup(chooseVmToggleGroup);
            item.setSelected(Start.virtualMachineProperty.getValue() == vm);
            items.add(item);
        }
        items.add(new SeparatorMenuItem());

        MenuItem createNewVM = new MenuItem("New VM");
        createNewVM.setAccelerator(KeyCombination.valueOf("Ctrl+N"));
        createNewVM.setOnAction(e -> createNewVM());
        items.add(createNewVM);
    }

    private void reloadToolbar() {
        toolBar.getItems().clear();
        if (!Start.virtualMachineProperty.isNull().get()) {
            VirtualMachine vm = Start.virtualMachineProperty.getValue();
            toolBar.getItems().addAll(vm.getType().getToolBarElements());
        }
    }

    @FXML
    private void createNewVM() {
        Start.createNewWindow("/eu/shooktea/vmsm/view/fxml/NewVM.fxml", "New VM", true);
    }

    @FXML
    private void exit() {
        System.exit(0);
    }

    @FXML
    private void addressEnterPressed() {
        String address = addressField.getText();
        if (!address.startsWith("http://") && !address.startsWith("https://")) {
            address = "http://" + address;
        }
        webEngine.load(address);
    }

    @FXML
    private void goToHomeUrl() {
        if (Start.virtualMachineProperty.isNull().get()) return;
        VirtualMachine vm = Start.virtualMachineProperty.getValue();
        if (vm.getPageRoot() == null) return;
        URL url = vm.getPageRoot();
        addressField.setText(url.toString());
        webEngine.load(url.toString());
    }

    private void reloadGUI(ObservableValue<? extends VirtualMachine> observable, VirtualMachine oldValue, VirtualMachine newValue) {
        reloadGUI();
    }
}
