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

import eu.shooktea.vmsm.Storage;
import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.Status;
import eu.shooktea.vmsm.view.controller.NewVM;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Representation of Vagrant type of virtual machine. It required from root directory to contain .vagrant/machines directory.
 */
public class Vagrant extends VMType {
    Vagrant() {
        super();
        this.typeName = new SimpleStringProperty("Vagrant");
        this.creationInfo = new SimpleStringProperty("Main path contains .vagrant/machines directory.");
        update(null);
    }

    @Override
    protected String checkRootFile(File file) {
        if (file == null) return "You haven't selected a file";
        if (!file.exists()) return "Path does not exist.";
        if (!file.isDirectory()) return "Path is not a directory.";

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
    public List<ImageView> getQuickGuiButtons() {
        List<ImageView> ret = super.getQuickGuiButtons();
        if (previousUpdateVm == null) return ret;

        ImageView switchStatus = Toolkit.createQuickGuiButton(previousUpdateVm.getStatus().getResourceName(), previousUpdateVm.getStatus().getTooltipText());
        switchStatus.setOnMouseClicked(e -> statusIconClicked());

        ret.add(switchStatus);
        return ret;
    }

    @Override
    public Optional<MenuItem> getMenuItem(VirtualMachine vm) {
        Menu root = new Menu(vm.getName(), Toolkit.createMenuImage("vagrant_icon.png"));

        MenuItem on = new MenuItem("Switch on", Toolkit.createMenuImage("play.png"));
        on.setOnAction(e -> this.switchMachine(vm, "up"));
        on.setDisable(vm.getStatus() != Status.STOPPED);

        MenuItem reset = new MenuItem("Reset", Toolkit.createMenuImage("play_all.png"));
        reset.setOnAction(e -> this.switchMachine(vm, "reload"));
        reset.setDisable(vm.getStatus() != Status.RUNNING);

        MenuItem off = new MenuItem("Switch off", Toolkit.createMenuImage("stop.png"));
        off.setOnAction(e -> this.switchMachine(vm, "halt"));
        off.setDisable(vm.getStatus() != Status.RUNNING);

        root.getItems().addAll(on, reset, off);
        return Optional.of(root);
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
    public String[] getModules() {
        return new String[]{
            "Magento", "SSH", "MySQL"
        };
    }

    private VirtualMachine previousUpdateVm = null;
    private boolean isMachineStateChanging = false;

    /**
     * Searches for all Vagrant virtual machines that have not yet been added to VMSM.
     */
    public static void searchUnregisteredVms() {
        try {
            ProcessBuilder builder = new ProcessBuilder("vagrant", "global-status");
            Process process = builder.start();
            new Thread(() -> {
                try {
                    List<String> entries = new ArrayList<>();
                    Scanner sc = new Scanner(process.getInputStream());
                    Thread thr = new Thread(() -> {
                        boolean start = false;
                        boolean stop = false;
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine().trim();
                            if (line.startsWith("------")) start = true;
                            else if (line.isEmpty()) stop = true;
                            else if (start && !stop) {
                                String[] entry = line.split("\\s+");
                                String directory = entry[4];
                                entries.add(directory);
                            }
                        }
                    });
                    thr.start();
                    process.waitFor();
                    thr.interrupt();
                    thr.join(1000);
                    Platform.runLater(() -> checkGlobalVagrantMachines(entries));
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

    private static void checkGlobalVagrantMachines(List<String> directories) {
        List<String> vms = Storage.getVmList()
                .stream()
                .filter(vm -> vm.getType().getTypeName().equals("Vagrant"))
                .map(VirtualMachine::getMainPath)
                .map(File::toString)
                .collect(Collectors.toList());

        List<String> ignored = Storage.getIgnoredVagrantMachines();

        ignored.removeIf(elem -> !directories.contains(elem));
        directories.removeIf(ignored::contains);
        directories.removeIf(vms::contains);

        for (String dir : directories) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("New VM detected");
            alert.setHeaderText("Virtual machine detected");
            alert.setContentText("VMSM detected a Vagrant machine in " + dir + ". Do you want to add it to your VMs?");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            ButtonType add = new ButtonType("Add VM", ButtonBar.ButtonData.APPLY);
            ButtonType notAdd = new ButtonType("Do not add", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType ignore = new ButtonType("Ignore");

            alert.getButtonTypes().setAll(add, notAdd, ignore);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == add) {
                NewVM.openNewVmWindow("Vagrant", dir);
            }
            else if (result.get() == ignore) {
                ignored.add(dir);
            }

            Storage.saveAll();
        }
    }
}
