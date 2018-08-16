package eu.shooktea.datamodel;

import java.util.*;
import java.util.stream.Stream;

public class DataModelMap extends AbstractMap<String, DataModelValue> implements DataModelValue {

    public DataModelMap(Map map) {
        this.map = new LinkedHashMap<>();
        for (Object key : map.keySet()) {
            this.map.put(key.toString(), DataModelConverter.convert(map.get(key)));
        }
    }

    public DataModelMap() {
        this(new LinkedHashMap<>());
    }

    @Override
    public DataModelType getType() {
        return DataModelType.MAP;
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

    public void removeKeys(String... keys) {
        for (String key : keys) remove(key);
    }

    public String getString(String key) {
        return get(key).<String>toPrimitive().getContent();
    }

    @Override
    public Object toStorageObject() {
        Map<String, Object> ret = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            ret.put(key, map.get(key).toStorageObject());
        }
        return ret;
    }

    public DataModelValue put(String key, Object ob) {
        return put(key, DataModelConverter.convert(ob));
    }

    public Stream<Entry<String, DataModelValue>> stream() {
        return entrySet().stream();
    }

    @Override
    public DataModelValue put(String key, DataModelValue val) {
        return map.put(key, val);
    }

    public void put(Map.Entry<String, DataModelValue> entry) {
        map.put(entry.getKey(), entry.getValue());
    }

    @Override
    public Set<Map.Entry<String, DataModelValue>> entrySet() {
        return map.entrySet();
    }

    private final Map<String, DataModelValue> map;
}
