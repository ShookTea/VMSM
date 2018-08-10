package eu.shooktea.vmsm.vmtype;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.File;
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
                "Magento",
                "MySQL",
        };
    }

    @Override
    public Optional<MenuItem> getMenuItem(VirtualMachine vm) {
        return Optional.of(createMenuItem(vm));

    }

    private MenuItem createMenuItem(VirtualMachine vm) {
        MenuItem up = new MenuItem("Up", Toolkit.createMenuImage("play.png"));
        MenuItem upBuild = new MenuItem("Up with --build", Toolkit.createMenuImage("run.png"));
        MenuItem stop = new MenuItem("Stop", Toolkit.createMenuImage("stop.png"));
        MenuItem kill = new MenuItem("Kill", Toolkit.createMenuImage("red_ball.png"));

        Menu root = new Menu(vm.getName(), Toolkit.createMenuImage("docker_logo.png"));
        root.getItems().addAll(
                up, upBuild, stop, kill
        );
        return root;
    }
}
