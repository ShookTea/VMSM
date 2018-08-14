package eu.shooktea.yaml;

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlMap extends YamlValue {

    public YamlMap(Map<String, Object> map) {
        super(YamlType.MAP);
        this.map = map;
    }

    public YamlMap() {
        this(new LinkedHashMap<>());
    }

    @Override
    public String toString() {
        return "YamlMap{" +
                "map=" + map +
                '}';
    }

    private final Map<String, Object> map;
}
