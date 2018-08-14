package eu.shooktea.vmsm.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

abstract public class AbstractFormat {
    public abstract void load(File file) throws IOException;
    public abstract void save(File file) throws IOException;

    public void load() throws IOException
    {
        this.load(getConfigFile());
    }

    public void save() throws IOException
    {
        this.save(getConfigFile());
    }

    public static File getConfigFile() throws IOException {
        File configDir = getConfigRootDirectory();
        File returnFile = null;

        String[] jsonFiles = configDir.list((dir, name) -> name.equals("config.json"));
        String[] yamlFiles = configDir.list((dir, name) -> name.equals("config.yml"));
        if (jsonFiles != null && jsonFiles.length != 0) {
            returnFile = new File(configDir, jsonFiles[0]);
        }
        else if (yamlFiles != null && yamlFiles.length != 0){
            returnFile = new File(configDir, yamlFiles[0]);
        }
        else {
            returnFile = new File("config.yml");
        }
        final String returnFileName = returnFile.getName();

        File[] files = configDir.listFiles();
        if (files == null) files = new File[0];
        Arrays.stream(files)
                .filter(f -> !f.getName().equals(returnFileName))
                .forEach(File::delete);

        if (!returnFile.exists()) returnFile.createNewFile();
        return returnFile;
    }

    public static File getConfigRootDirectory() {
        String homePath = System.getProperty("user.home");
        File home = new File(homePath);
        File root = new File(home, ".vmsm");
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }
}
