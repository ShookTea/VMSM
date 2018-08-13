package eu.shooktea.vmsm.vmtype;

import eu.shooktea.fxtoolkit.FXToolkit;
import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class DockerCompose extends VMType {
    DockerCompose() {
        super();
        this.typeName = new SimpleStringProperty("Docker Compose");
        this.creationInfo = new SimpleStringProperty("Main path contains docker-compose.yml file.");
        update(null);
    }

    @Override
    protected String checkRootFile(File file) {
        if (file == null) return "You haven't selected a directory";
        if (!file.exists()) return "File does not exist.";
        if (!file.isDirectory()) return "File is not a directory.";
        File[] files = file.listFiles(
                (dir, name) -> name.equals("docker-compose.yml") || name.equals("docker-compose.yaml")
        );
        if (files == null || files.length < 1) return "There is no file called 'docker-compose.yml' in this directory.";
        else if (files.length > 1) return "There are too many 'docker-compose.yml' files in this directory.";
        else return "";
    }

    @Override
    public String[] getModules() {
        return new String[] {
                "Docker Compose",
                "Magento",
                "MySQL",
        };
    }

    @Override
    public Optional<MenuItem> getMenuItem(VirtualMachine vm) {
        return Optional.of(createMenuItem(vm));
    }

    public File getDockerComposeFile() {
        File root = VM.getOrThrow().getMainPath();
        File a = new File(root, "docker-compose.yml");
        if (a.exists()) return a;
        a = new File(root, "docker-compose.yaml");
        if (a.exists()) return a;
        return null;
    }

    private MenuItem createMenuItem(VirtualMachine vm) {
        MenuItem up = new MenuItem("Up", Toolkit.createMenuImage("play.png"));
        up.setOnAction(e -> runCommand("VM is up", "docker-compose", "up", "-d"));
        MenuItem upBuild = new MenuItem("Up with --build", Toolkit.createMenuImage("run.png"));
        upBuild.setOnAction(e -> runCommand("VM is up", "docker-compose", "up", "-d", "--build"));
        MenuItem stop = new MenuItem("Stop", Toolkit.createMenuImage("stop.png"));
        stop.setOnAction(e -> runCommand("VM is down", "docker-compose", "stop"));
        MenuItem kill = new MenuItem("Kill", Toolkit.createMenuImage("red_ball.png"));
        kill.setOnAction(e -> runCommand("VM is down", "docker-compose", "kill"));

        Menu root = new Menu(vm.getName(), Toolkit.createMenuImage("docker_logo.png"));
        root.getItems().addAll(
                up, upBuild, stop, kill
        );
        return root;
    }

    private void runCommand(String message, String... command) {
        VirtualMachine vm = VM.getOrThrow();
        try {
            ProcessBuilder builder = new ProcessBuilder(command).directory(vm.getMainPath());
            Process process = builder.start();
            FXToolkit.runOnFxThread(() -> View.showMessage(message))
                    .after(() -> {
                        try {
                            System.out.println("Before process.waitingFor()");
                            process.waitFor();
                            System.out.println("After process.waitingFor()");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
