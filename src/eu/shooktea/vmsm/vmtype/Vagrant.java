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

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.VirtualMachine.Status;
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
        update(null);
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
        if (vm == null) {
            return;
        }
        if (isMachineStateChanging) {
            vm.setStatus(Status.UNDEFINED);
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
                            vm.setStatus(Status.RUNNING);
                        if (!isMachineStateChanging && (line.contains("The VM is powered off.") || line.contains("The VM is in an aborted state.")))
                            vm.setStatus(Status.STOPPED);
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
            previousUpdateVm = vm;
            if (previousUpdateVm != null) previousUpdateVm.setStatus(Status.UNDEFINED);
        }
        updateStatus(vm, vmChange);
    }

    @Override
    public Optional<ImageView[]> getQuickGuiButtons() {
        if (previousUpdateVm == null) return Optional.empty();
        ImageView switchStatus = Toolkit.createQuickGuiButton(previousUpdateVm.getStatus().getResourceName(), previousUpdateVm.getStatus().getTooltipText());
        switchStatus.setOnMouseClicked(e -> statusIconClicked());
        return Optional.of(new ImageView[] {switchStatus});
    }

    private void statusIconClicked() {
        if (previousUpdateVm == null) return;
        Status status = previousUpdateVm.getStatus();
        if (status == Status.UNDEFINED) return;

        if (status == Status.RUNNING) switchMachine(previousUpdateVm, "halt");
        else if (status == Status.STOPPED) switchMachine(previousUpdateVm, "up");
    }

    private void switchMachine(VirtualMachine vm, String action) {
        try {
            isMachineStateChanging = true;
            vm.setStatus(Status.UNDEFINED);
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
    public Optional<String[]> getModules() {
        return Optional.of(new String[]{
            "Magento", "SSH", "MySQL"
        });
    }

    private VirtualMachine previousUpdateVm = null;
    private boolean isMachineStateChanging = false;
}
