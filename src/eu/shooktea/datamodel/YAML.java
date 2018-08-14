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

    public DataModelValue load(String s) {
        return DataModelValue.fromObject(yaml.load(s));
    }

    public DataModelValue load(InputStream is) {
        return DataModelValue.fromObject(yaml.load(is));
    }

    public DataModelValue load(Reader r) {
        return DataModelValue.fromObject(yaml.load(r));
    }

    public DataModelValue load(File f) throws FileNotFoundException {
        return this.load(new FileInputStream(f));
    }

    public String toString(DataModelValue val) {
        return yaml.dump(val.toStorageObject());
    }

    public void toWriter(DataModelValue val, Writer writer) {
        yaml.dump(val.toStorageObject(), writer);
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
