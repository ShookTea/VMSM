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

    public YamlMap(YamlMap m) {
        this(m.map);
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

    public void put(String key, Object val) {
        map.put(key, YamlValue.fromObject(val));
    }

    public void put(Map.Entry<String, YamlValue> entry) {
        map.put(entry.getKey(), entry.getValue());
    }

    public YamlValue get(String key) {
        return map.get(key);
    }

    public YamlValue getOrDefault(String key, YamlValue val) {
        return map.getOrDefault(key, val);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public void remove(String key) {
        map.remove(key);
    }

    public void removeKeys(String... keys) {
        for (String key : keys) remove(key);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public boolean containsValue(YamlValue value) {
        return map.containsValue(value);
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
