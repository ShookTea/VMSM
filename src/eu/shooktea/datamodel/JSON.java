package eu.shooktea.datamodel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Function;

public class JSON implements DataSupplier {
    @Override
    public DataModelMap load(String s) {
        return converter().apply(new JSONObject(s)).toMap();
    }

    @Override
    public String store(DataModelMap dmm) {
        return new JSONObject(dmm.toStorageObject()).toString();
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
        DataModelValue.setConverter(instance.converter());
        return instance;
    }

    private static JSON instance = null;
}
