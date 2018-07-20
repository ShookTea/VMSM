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
package eu.shooktea.vmsm.vmtype;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Vagrant extends VMType {
    public Vagrant() {
        super();
        this.typeName = new SimpleStringProperty("Vagrant");
        this.creationInfo = new SimpleStringProperty("Main path contains .vagrant/machines directory.");
        this.toolBarElements = new SimpleListProperty(FXCollections.observableArrayList(createToolBarElements()));
        update(null);
    }

    private List<Node> createToolBarElements() {
        ImageView vagrantIcon = Start.createToolbarImage("vagrant_icon.png");
        statusIcon = Start.createToolbarImage(statusProperty.get().getResourceName());
        Tooltip tooltip = new Tooltip(statusProperty.get().getTooltipText());
        Tooltip.install(statusIcon, tooltip);

        statusProperty.addListener(((observable, oldValue, newValue) -> Platform.runLater(() -> {
            statusIcon.setImage(Start.createToolbarImage(newValue.getResourceName()).getImage());
            tooltip.setText(newValue.getTooltipText());
        })));

        statusIcon.setOnMouseClicked((e) -> statusIconClicked());

        return Arrays.asList(vagrantIcon, statusIcon);
    }

    @Override
    protected String checkRootFile(File file) {
        if (!file.exists()) {
            return "Path does not exist.";
        }
        if (!file.isDirectory()) {
            return "Path is not a directory.";
        }
        String notCorrect = "Path is not a correct Vagrant root directory.";
        if (file.listFiles(f -> f.getName().equals(".vagrant")).length == 0) {
            return notCorrect;
        }
        File dotVagrant = file.listFiles(f -> f.getName().equals(".vagrant"))[0];
        if (dotVagrant.listFiles(f -> f.getName().equals("machines")).length == 0) {
            return notCorrect;
        }
        return "";
    }

    private void updateStatus(VirtualMachine vm, boolean afterVmChange) {
        if (isMachineStateChanging || vm == null) {
            statusProperty.setValue(Status.UNDEFINED);
            return;
        }
        try {
            if (afterVmChange) isMachineStateChanging = true;
            File root = vm.getMainPath();
            ProcessBuilder builder = new ProcessBuilder("vagrant", "status").directory(root);
            Process process = builder.start();
            new Thread(() -> {
                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        if (!isMachineStateChanging && line.contains("The VM is running."))
                            statusProperty.setValue(Status.RUNNING);
                        if (!isMachineStateChanging && (line.contains("The VM is powered off.") || line.contains("The VM is in an aborted state.")))
                            statusProperty.setValue(Status.STOPPED);
                    }
                    process.waitFor();
                    if (afterVmChange) isMachineStateChanging = false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public void update(VirtualMachine vm) {
        boolean vmChange = vm != previousUpdateVm;
        if (vmChange) {
            statusProperty.setValue(Status.UNDEFINED);
            previousUpdateVm = vm;
        }
        updateStatus(vm, vmChange);
    }

    private void statusIconClicked() {
        if (previousUpdateVm == null) return;
        Status status = statusProperty.get();
        if (status == Status.UNDEFINED) return;

        if (status == Status.RUNNING) switchMachine(previousUpdateVm, "halt");
        else if (status == Status.STOPPED) switchMachine(previousUpdateVm, "up");
    }

    private void switchMachine(VirtualMachine vm, String action) {
        try {
            isMachineStateChanging = true;
            statusProperty.setValue(Status.UNDEFINED);
            ProcessBuilder builder = new ProcessBuilder("vagrant", action).directory(vm.getMainPath());
            Process process = builder.start();
            new Thread(() -> {
                try {
                    process.waitFor();
                    isMachineStateChanging = false;
                    update(vm);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public Optional<Menu> getMenu() {
        MenuItem launch = new MenuItem("Start VM", Start.createMenuImage("play.png"));
        launch.disableProperty().bind(statusProperty.isNotEqualTo(Status.STOPPED));
        launch.setOnAction((event) -> switchMachine(previousUpdateVm, "up"));

        MenuItem restart = new MenuItem("Restart VM", Start.createMenuImage("play_all.png"));
        restart.disableProperty().bind(statusProperty.isNotEqualTo(Status.RUNNING));
        restart.setOnAction((event) -> switchMachine(previousUpdateVm, "reload"));

        MenuItem stop = new MenuItem("Stop VM", Start.createMenuImage("stop.png"));
        stop.disableProperty().bind(statusProperty.isNotEqualTo(Status.RUNNING));
        stop.setOnAction((event) -> switchMachine(previousUpdateVm, "halt"));

        Menu menu = new Menu(
                "Vagrant",
                Start.createMenuImage("vagrant_icon.png"),
                launch, restart, stop
        );
        return Optional.of(menu);
    }

    @Override
    public Optional<String[]> getModules() {
        return Optional.of(new String[]{
            "Magento"
        });
    }

    private VirtualMachine previousUpdateVm = null;

    private ObjectProperty<Status> statusProperty = new SimpleObjectProperty<>(Status.UNDEFINED);
    private ImageView statusIcon;
    private boolean isMachineStateChanging = false;

    public enum Status {
        RUNNING, STOPPED, UNDEFINED;

        public String getResourceName() {
            switch (this) {
                case RUNNING:   return "green_ball.png";
                case STOPPED:   return "red_ball.png";
                case UNDEFINED: return "yellow_ball.png";
                default: throw new RuntimeException();
            }
        }

        public String getTooltipText() {
            switch (this) {
                case RUNNING:   return "Vagrant machine is currently switched on.";
                case STOPPED:   return "Vagrant machine is currently switched off.";
                case UNDEFINED: return "Vagrant machine state is currently unknown.";
                default: throw new RuntimeException();
            }
        }
    }
}
