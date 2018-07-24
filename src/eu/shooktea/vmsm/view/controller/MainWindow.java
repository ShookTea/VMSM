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

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.reactfx.value.Val;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MainWindow {
    @FXML public MenuBar menuBar;
    @FXML public ToolBar toolBar;
    @FXML private HBox browserContainer;
    @FXML private TextField addressField;
    @FXML private ProgressBar progressBar;
    @FXML private Menu vmListMenu;
    @FXML private ImageView homeButton;
    @FXML private Menu virtualMachineTypeMenu;
    @FXML private MenuItem goToRootWebpageMenuItem;

    public Browser browser;
    private ToggleGroup chooseVmToggleGroup = new ToggleGroup();
    private BrowserProgressBar progressListener;

    @FXML
    private void initialize() {
        VM.addListener(this::reloadGUI);

        progressListener = new BrowserProgressBar();
        browser = new Browser(BrowserType.LIGHTWEIGHT, BrowserContext.defaultContext());
        browser.addLoadListener(progressListener);
        browser.addConsoleListener(e -> JsConsole.outputText.setValue(JsConsole.outputText.getValue() + e.getMessage() + "\n"));
        progressListener.somethingHasChangedProperty().addListener(((observable, oldValue, newValue) -> addressField.setText(browser.getURL())));
        BrowserView view = new BrowserView(browser);
        browserContainer.getChildren().clear();
        browserContainer.getChildren().add(view);
        HBox.setHgrow(view, Priority.ALWAYS);
        browser.loadURL("google.pl");

        bindProgressBar();
        bindHomeButton();

        chooseVmToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            RadioMenuItem item = (RadioMenuItem)newValue;
            String name = item.getText();
            VirtualMachine chosenMachine = Storage.getVmList().stream()
                    .filter(vm -> vm.getName().equals(name))
                    .findFirst()
                    .get();
            VM.set(chosenMachine);
        }));
    }

    public void close() {
        Runnable dispose = () -> {
            browser.dispose();
            System.exit(0);
        };
        if (isWindows()) new Thread(dispose).start();
        else Platform.runLater(dispose);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    private void bindProgressBar() {
        progressBar.setStyle("-fx-accent: blue;");
        progressBar.progressProperty().bind(progressListener.progressProperty());
    }

    private void bindHomeButton() {
        ColorAdjust effect = (ColorAdjust)homeButton.getEffect();
        effect.saturationProperty().bind(
                Val.flatMap(VM.getProperty(), VirtualMachine::pageRootProperty)
                .map(url -> url == null ? -1.0 : 0.0)
                .orElseConst(-1.0)
        );
        goToRootWebpageMenuItem.disableProperty().bind(
                Val.flatMap(VM.getProperty(), VirtualMachine::pageRootProperty)
                        .map(Objects::isNull)
                        .orElseConst(true)
        );
    }

    public void reloadGUI() {
        View.stage().setTitle(
                VM.isSet() ? "VMSM - " + VM.get().getName() : "VMSM"
        );
        reloadMenu();
        reloadToolbar();
    }

    private void reloadMenu() {
        ObservableList<MenuItem> items = vmListMenu.getItems();
        items.clear();
        for (VirtualMachine vm : Storage.getVmList()) {
            RadioMenuItem item = new RadioMenuItem(vm.getName());
            item.setToggleGroup(chooseVmToggleGroup);
            item.setSelected(VM.get() == vm);
            items.add(item);
        }
        items.add(new SeparatorMenuItem());

        MenuItem createNewVM = new MenuItem("New VM...");
        createNewVM.setAccelerator(KeyCombination.valueOf("Ctrl+N"));
        createNewVM.setOnAction(NewVM::openNewVmWindow);
        items.add(createNewVM);

        MenuItem vmManager = new MenuItem("VM Manager...");
        vmManager.setOnAction(VmManager::openVmManagerWindow);
        items.add(vmManager);

        if (VM.isNotEqual(previousMachine)) {
            previousMachine = VM.get();
            VMType type = previousMachine.getType();
            ObservableList<MenuItem> vmTypeItems = virtualMachineTypeMenu.getItems();
            vmTypeItems.clear();
            if (previousMachine != null) type.getMenu().ifPresentOrElse(menu -> {
                virtualMachineTypeMenu.setText(menu.getText());
                virtualMachineTypeMenu.setGraphic(menu.getGraphic());
                virtualMachineTypeMenu.setAccelerator(menu.getAccelerator());
                ObservableList<MenuItem> newItems = menu.getItems();
                List<MenuItem> transfer = new ArrayList<>(newItems);
                for(MenuItem item : transfer) {
                    newItems.remove(item);
                    vmTypeItems.add(item);
                }
                if (type.getModules().isPresent()) {
                    MenuItem modulesDialog = new MenuItem("Managing modules...", Toolkit.createMenuImage("run.png"));
                    modulesDialog.setOnAction(ModuleConfig::openModuleConfigWindow);
                    vmTypeItems.addAll(new SeparatorMenuItem(), modulesDialog);
                }
                virtualMachineTypeMenu.setVisible(true);
            }, () -> {
                if (type.getModules().isPresent()) {
                    MenuItem modulesDialog = new MenuItem("Managing modules...", Toolkit.createMenuImage("run.png"));
                    modulesDialog.setOnAction(ModuleConfig::openModuleConfigWindow);
                    vmTypeItems.add(modulesDialog);
                    virtualMachineTypeMenu.setVisible(true);
                }
                else {
                    virtualMachineTypeMenu.setVisible(false);
                }
            });
        }
    }

    private void reloadToolbar() {
        toolBar.getItems().clear();
        if (VM.isSet()) {
            VirtualMachine vm = VM.get();
            toolBar.getItems().addAll(vm.getType().getToolBarElements());
            vm.getModules()
                    .sorted(Comparator.comparingInt(Module::toolbarOrder))
                    .forEach(Module::reloadToolbar);
        }
    }

    @FXML
    private void addressEnterPressed() {
        String address = addressField.getText();
        if (!address.startsWith("http://") && !address.startsWith("https://")) {
            address = "http://" + address;
        }
        browser.loadURL(address);
    }

    @FXML
    private void goToHomeUrl() {
        if (!VM.isSet()) return;
        VirtualMachine vm = VM.get();
        if (vm.getPageRoot() == null) return;
        URL url = vm.getPageRoot();
        addressField.setText(url.toString());
        browser.loadURL(url.toString());
    }

    @FXML
    public void reloadWebpage() {
        if (!this.addressField.getText().trim().isEmpty()) browser.reload();
    }

    private VirtualMachine previousMachine = null;

    public void openJsConsole() {
        JsConsole.openJsConsole();
    }
}
