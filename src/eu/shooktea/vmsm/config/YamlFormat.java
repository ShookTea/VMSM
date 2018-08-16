package eu.shooktea.vmsm.config;

import eu.shooktea.datamodel.DataModelMap;
import eu.shooktea.datamodel.DataModelPrimitive;
import eu.shooktea.datamodel.YAML;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class YamlFormat extends AbstractFormat {

    /* check format version before loading */
    @Override
    protected void load(File file) {
        DataModelMap root = YAML.instance().load(file);

        String version = root.getOrDefault("version", new DataModelPrimitive<>("1.0")).<String>toPrimitive().getContent();
        YamlVersion yamlVersion = YamlVersion.by(version);
        yamlVersion.getYamlInterface().load(root);
    }

    /* always save in newest format version possible */
    @Override
    protected void save(File file) {
        DataModelMap root = new DataModelMap();
        YamlVersion version = YamlVersion.newest();
        root.put("version", version.getVersion());
        version.getYamlInterface().save(root);

        YAML.instance().store(root, file);
    }

    @Override
    protected File createSaveFile(File directory) {
        return new File(directory, "config.yml");
    }

    private enum YamlVersion {
        VERSION_1_0(new Yaml_1_0(), "1.0", true);

        YamlVersion(YamlVersionInterface yaml, String version, boolean isNewest) {
            this.version = version;
            this.yvi = yaml;
            this.isNewest = isNewest;
        }

        public YamlVersionInterface getYamlInterface() {
            return yvi;
        }

        public String getVersion() {
            return version;
        }

        public boolean isNewest() {
            return isNewest;
        }

        private final YamlVersionInterface yvi;
        private final String version;
        private final boolean isNewest;

        static YamlVersion newest() {
            return Arrays.stream(YamlVersion.values())
                    .filter(YamlVersion::isNewest)
                    .findFirst().get();
        }

        static YamlVersion by(String version) {
            return Arrays.stream(YamlVersion.values())
                    .filter(v -> v.getVersion().equalsIgnoreCase(version))
                    .findFirst().get();
        }
    }
}
