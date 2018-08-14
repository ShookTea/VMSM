package eu.shooktea.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class YAML {
    private YAML() {
        this.yaml = new Yaml();
        try {
            FileInputStream fis = new FileInputStream("/home/nkowalik/docker/Magento/docker-compose.yml");
            Object ob = yaml.load(fis);
            YamlValue value = YamlValue.fromObject(ob);
            System.out.println(value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
