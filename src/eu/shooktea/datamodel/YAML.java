package eu.shooktea.datamodel;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class YAML {
    private YAML() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    public YamlValue load(String s) {
        return YamlValue.fromObject(yaml.load(s));
    }

    public YamlValue load(InputStream is) {
        return YamlValue.fromObject(yaml.load(is));
    }

    public YamlValue load(Reader r) {
        return YamlValue.fromObject(yaml.load(r));
    }

    public YamlValue load(File f) throws FileNotFoundException {
        return this.load(new FileInputStream(f));
    }

    public String toString(YamlValue val) {
        return yaml.dump(val.toYamlObject());
    }

    public void toWriter(YamlValue val, Writer writer) {
        yaml.dump(val.toYamlObject(), writer);
    }

    private final Yaml yaml;

    //===================================================

    public static YAML instance() {
        if (instance == null)
            instance = new YAML();
        return instance;
    }

    private static YAML instance = null;
}
