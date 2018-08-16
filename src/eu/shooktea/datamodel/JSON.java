package eu.shooktea.datamodel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JSON implements DataSupplier {
    @Override
    public DataModelMap load(String s) {
        s = s.trim();
        if (s.isEmpty()) s = "{}";
        return converter().apply(new JSONObject(s)).toMap();
    }

    @Override
    public String store(DataModelMap dmm) {
        System.out.println("Before: " + dmm.toString());
        System.out.println("After:  " + dmm.toStorageObject().toString());
        return convertMapToJson(dmm).toString();
    }

    private JSONObject convertMapToJson(DataModelMap map) {
        return (JSONObject)convertToJson(map);
    }

    private Object convertToJson(Object ob) {
        if (ob instanceof DataModelValue) {
            DataModelValue dmv = (DataModelValue)ob;
            if (dmv.isPrimitive())
                return dmv.toPrimitive().getContent().toString();
            if (dmv.isList())
                return new JSONArray(
                        dmv.toList()
                        .stream()
                        .map(this::convertToJson)
                        .collect(Collectors.toList())
                );
            if (dmv.isMap())
                return new JSONObject(
                        dmv.toMap()
                        .stream()
                        .map(e -> new HashMap.SimpleEntry<>(e.getKey(), convertToJson(e.getValue())))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
                );
            return null;
        }
        else {
            return ob.toString();
        }
    }

    @Override
    public Function<Object, DataModelValue> converter() {
        return ob -> {
            if (ob instanceof JSONObject)
                return new DataModelMap(((JSONObject) ob).toMap());
            if (ob instanceof JSONArray)
                return new DataModelList(((JSONArray) ob).toList());
            return genericConverter.apply(ob);
        };
    }

    public static JSON instance() {
        if (instance == null)
            instance = new JSON();
        DataModelConverter.setConverter(instance.converter());
        return instance;
    }

    private static JSON instance = null;
}
