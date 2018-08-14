package eu.shooktea.vmsm.config;

import eu.shooktea.yaml.YamlMap;

import java.io.File;
import java.io.IOException;

public class YamlFormat extends AbstractFormat {

    /* check format version before loading */
    @Override
    protected void load(File file) throws IOException {

    }

    /* always save in newest format version possible */
    @Override
    protected void save(File file) throws IOException {
        YamlMap root = new YamlMap();
        root.put("version", "1.0");

    }

    @Override
    protected File createSaveFile(File directory) {
        return new File(directory, "config.yml");
    }
}
