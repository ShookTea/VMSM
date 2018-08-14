package eu.shooktea.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

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

    private final Yaml yaml;

    //===================================================

    public static YAML instance() {
        if (instance == null)
            instance = new YAML();
        return instance;
    }

    private static YAML instance = null;
}
