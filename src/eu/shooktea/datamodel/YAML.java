package eu.shooktea.datamodel;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class YAML implements DataSupplier {
    private YAML() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    @Override
    public DataModelMap load(String s) {
        return yaml.load(s);
    }

    @Override
    public DataModelMap load(InputStream is) {
        return yaml.load(is);
    }

    @Override
    public DataModelMap load(Reader r) {
        return yaml.load(r);
    }

    @Override
    public String store(DataModelMap dmm) {
        return yaml.dump(dmm);
    }

    @Override
    public String store(DataModelMap dmm, Writer w) {
        yaml.dump(dmm, w);
        return store(dmm);
    }

    @Override
    public Function<Object, DataModelValue> converter() {
        return ob -> {
            if (ob instanceof DataModelValue)
                return (DataModelValue)ob;
            if (ob instanceof Map)
                return new DataModelMap((Map)ob);
            if (ob instanceof List)
                return new DataModelList((List)ob);
            return genericConverter.apply(ob);
        };
    }

    private final Yaml yaml;

    //===================================================

    public static YAML instance() {
        if (instance == null)
            instance = new YAML();
        DataModelValue.setConverter(instance.converter());
        return instance;
    }

    private static YAML instance = null;
}
