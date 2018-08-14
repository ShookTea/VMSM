package eu.shooktea.yaml;

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlMap extends YamlValue {

    public YamlMap(Map map) {
        super(YamlType.MAP);
        this.map = new LinkedHashMap<>();
        for (Object key : map.keySet()) {
            this.map.put(key.toString(), YamlValue.fromObject(map.get(key)));
        }
    }

    public YamlMap() {
        this(new LinkedHashMap<>());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("YamlMap{");
        boolean start = true;
        for (String key : map.keySet()) {
            builder.append(start ? "" : ";").append(key).append("=").append(map.get(key).toString());
            start = false;
        }
        return builder.append("}").toString();
    }

    private final Map<String, YamlValue> map;
}
