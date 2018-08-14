package eu.shooktea.datamodel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DataModelMap extends DataModelValue implements Iterable<Map.Entry<String, DataModelValue>> {

    public DataModelMap(Map map) {
        super(DataModelType.MAP);
        this.map = new LinkedHashMap<>();
        for (Object key : map.keySet()) {
            this.map.put(key.toString(), DataModelValue.fromObject(map.get(key)));
        }
    }

    public DataModelMap(DataModelMap m) {
        this(m.map);
    }

    public DataModelMap() {
        this(new LinkedHashMap<>());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("DataModelMap{");
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
        map.put(key, DataModelValue.fromObject(val));
    }

    public void put(Map.Entry<String, DataModelValue> entry) {
        map.put(entry.getKey(), entry.getValue());
    }

    public DataModelValue get(String key) {
        return map.get(key);
    }

    public DataModelValue getOrDefault(String key, DataModelValue val) {
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

    public boolean containsValue(DataModelValue value) {
        return map.containsValue(value);
    }

    public Set<Map.Entry<String, DataModelValue>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Iterator<Map.Entry<String, DataModelValue>> iterator() {
        return entrySet().iterator();
    }

    public Stream<Map.Entry<String, DataModelValue>> stream() {
        return entrySet().stream();
    }

    private final Map<String, DataModelValue> map;
}
