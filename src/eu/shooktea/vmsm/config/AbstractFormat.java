package eu.shooktea.vmsm.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

abstract public class AbstractFormat {
    protected abstract void load(File file) throws IOException;
    protected abstract void save(File file) throws IOException;
    protected abstract File createSaveFile(File directory);

    public static void load() throws IOException {
        File config = getConfigFile();
        AbstractFormat format = getFormatForFile(config);
        if (format == null) throw new IOException("Unknown format for file " + config.toString());
        format.load(config);
    }

    public static void save() throws IOException {
        AbstractFormat format = getNewestSaveFormat();
        File dir = getConfigRootDirectory();
        File config = format.createSaveFile(dir);
        if (!config.exists()) config.createNewFile();

        final String configName = config.getName();
        File[] files = dir.listFiles();
        if (files == null) files = new File[0];
        Arrays.stream(files)
                .filter(File::isFile)
                .filter(f -> !f.getName().equals(configName))
                .forEach(File::delete);
        format.save(config);
    }

    private static File getConfigFile() throws IOException {
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
            returnFile = new File("config.json");
        }
        final String returnFileName = returnFile.getName();

        File[] files = configDir.listFiles();
        if (files == null) files = new File[0];
        Arrays.stream(files)
                .filter(File::isFile)
                .filter(f -> !f.getName().equals(returnFileName))
                .forEach(File::delete);

        if (!returnFile.exists()) returnFile.createNewFile();
        return returnFile;
    }

    private static AbstractFormat getFormatForFile(File f) {
        if (f.getName().endsWith(".json"))
            return new JsonFormat();
        return null;
    }

    private static AbstractFormat getNewestSaveFormat() {
        return new JsonFormat();
    }

    private static File getConfigRootDirectory() {
        String homePath = System.getProperty("user.home");
        File home = new File(homePath);
        File root = new File(home, ".vmsm");
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }
}
