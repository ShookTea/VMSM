package eu.shooktea.vmsm.config;

import java.io.File;
import java.io.IOException;

abstract public class AbstractFormat {
    public abstract void load(File file) throws IOException;
    public abstract void save(File file) throws IOException;

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
