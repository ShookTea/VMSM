package eu.shooktea.yaml;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class YamlMap extends YamlValue implements Iterable<Map.Entry<String, YamlValue>> {

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

    @Override
    public Object toYamlObject() {
        Map<String, Object> ret = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            ret.put(key, map.get(key).toYamlObject());
        }
        return ret;
    }

    public void put(String key, YamlValue val) {
        map.put(key, val);
    }

    public YamlValue get(String key) {
        return map.get(key);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public void remove(String key) {
        map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public Set<Map.Entry<String, YamlValue>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Iterator<Map.Entry<String, YamlValue>> iterator() {
        return entrySet().iterator();
    }

    public Stream<Map.Entry<String, YamlValue>> stream() {
        return entrySet().stream();
    }

    private final Map<String, YamlValue> map;
}
