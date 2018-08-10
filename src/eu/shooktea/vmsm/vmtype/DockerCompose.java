package eu.shooktea.vmsm.vmtype;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;

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
                "Magento"
        };
    }
}
