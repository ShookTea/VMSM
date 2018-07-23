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
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.reactfx.value.Val;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainWindow implements LoadListener {

    @FXML public MenuBar menuBar;
    @FXML public ToolBar toolBar;
    @FXML private WebView webView;
    @FXML private HBox browserContainer;
    @FXML private TextField addressField;
    @FXML private ProgressBar progressBar;
    @FXML private Menu vmListMenu;
    @FXML private ImageView homeButton;
    @FXML private Menu virtualMachineTypeMenu;

    public WebEngine webEngine;
    public Browser browser;
    private ToggleGroup chooseVmToggleGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        Start.virtualMachineProperty.addListener(((observable, oldValue, newValue) -> reloadGUI()));
        browser = new Browser(BrowserType.HEAVYWEIGHT, BrowserContext.defaultContext());
        browser.addLoadListener(this);
        BrowserView view = new BrowserView(browser);
        browserContainer.getChildren().clear();
        browserContainer.getChildren().add(view);
        HBox.setHgrow(view, Priority.ALWAYS);

        webEngine = webView.getEngine();
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> addressField.setText(newValue));
        webEngine.getLoadWorker().exceptionProperty().addListener(
                (observable, oldValue, newValue) -> displayErrorMessage(newValue));
        bindProgressBar();
        bindHomeButton();

        chooseVmToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            RadioMenuItem item = (RadioMenuItem)newValue;
            String name = item.getText();
            VirtualMachine chosenMachine = Storage.vmList.stream()
                    .filter(vm -> vm.getName().equals(name))
                    .findFirst()
                    .get();
            Start.virtualMachineProperty.setValue(chosenMachine);
        }));
    }

    public void close() {
        Runnable dispose = () -> {browser.dispose(); Platform.runLater(() -> Start.primaryStage.close());};
        if (isWindows()) new Thread(dispose).start();
        else Platform.runLater(dispose);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    private void displayErrorMessage(Throwable error) {
        if (error == null) return;
        String message = error.getMessage();
        webEngine.loadContent("<b>" + message + "</b>");
    }

    private void bindProgressBar() {
        progressBar.setStyle("-fx-accent: blue;");
        progressBar.progressProperty().bind(this.progress);
    }

    private void bindHomeButton() {
        ColorAdjust effect = (ColorAdjust)homeButton.getEffect();
        effect.saturationProperty().bind(
                Val.flatMap(Start.virtualMachineProperty, VirtualMachine::pageRootProperty)
                .map(url -> url == null ? -1.0 : 0.0)
                .orElseConst(-1.0)
        );
    }

    public void reloadGUI() {
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

        MenuItem createNewVM = new MenuItem("New VM...");
        createNewVM.setAccelerator(KeyCombination.valueOf("Ctrl+N"));
        createNewVM.setOnAction(NewVM::openNewVmWindow);
        items.add(createNewVM);

        MenuItem vmManager = new MenuItem("VM Manager...");
        vmManager.setOnAction(VmManager::openVmManagerWindow);
        items.add(vmManager);

        if (Start.virtualMachineProperty.get() != previousMachine) {
            previousMachine = Start.virtualMachineProperty.get();
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
                    MenuItem modulesDialog = new MenuItem("Managing modules...", Start.createMenuImage("run.png"));
                    modulesDialog.setOnAction(ModuleConfig::openModuleConfigWindow);
                    vmTypeItems.addAll(new SeparatorMenuItem(), modulesDialog);
                }
                virtualMachineTypeMenu.setVisible(true);
            }, () -> {
                if (type.getModules().isPresent()) {
                    MenuItem modulesDialog = new MenuItem("Managing modules...", Start.createMenuImage("run.png"));
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
        if (!Start.virtualMachineProperty.isNull().get()) {
            VirtualMachine vm = Start.virtualMachineProperty.getValue();
            toolBar.getItems().addAll(vm.getType().getToolBarElements());
            vm.getModules().forEach(module -> module.reloadToolbar());
        }
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

    @FXML
    public void reloadWebpage() {
        if (!this.addressField.getText().trim().isEmpty()) webEngine.reload();
    }

    public void goTo(String url) {
        addressField.setText(url);
        addressEnterPressed();
    }

    private VirtualMachine previousMachine = null;

    @Override
    public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
        addProgress();
    }

    @Override
    public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
        addProgress();
    }

    @Override
    public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
        subtractProgress();
    }

    @Override
    public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {
        resetProgress();
    }

    @Override
    public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {
        subtractProgress();
    }

    @Override
    public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {
        displayProgress();
    }

    private void addProgress() {
        if (framesLoading == 0) isMainFrameLoaded = false;
        framesLoading++;
        updateProgressProperty();
    }

    private void subtractProgress() {
        framesLoaded++;
        updateProgressProperty();
    }

    private void resetProgress() {
        isMainFrameLoaded = false;
        framesLoaded = 0;
        framesLoading = 0;
        updateProgressProperty();
    }

    private void displayProgress() {
        isMainFrameLoaded = true;
        updateProgressProperty();
    }

    private void updateProgressProperty() {
        Runnable r;
        if (isMainFrameLoaded) {
            r = () -> progress.setValue(framesLoaded / framesLoading);
        }
        else if (framesLoading > 0) {
            r = () -> progress.setValue(-1);
        }
        else {
            r = () -> progress.setValue(0);
        }
        Platform.runLater(r);
    }

    private int framesLoading = 0;
    private int framesLoaded = 0;
    private boolean isMainFrameLoaded = false;
    private DoubleProperty progress = new SimpleDoubleProperty(0.0);
}
