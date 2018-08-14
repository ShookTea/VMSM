package eu.shooktea.datamodel;

import java.util.List;
import java.util.Map;

public abstract class DataModelValue {
    public DataModelValue(DataModelType type) {
        this.type = type;
    }

    public DataModelType getType() {
        return type;
    }

    public boolean isMap() {
        return type == DataModelType.MAP;
    }

    public DataModelMap toMap() {
        if (!isMap() || !(this instanceof DataModelMap)) throw new RuntimeException("Value " + this + " is not a map!");
        return (DataModelMap)this;
    }

    public boolean isList() {
        return type == DataModelType.LIST;
    }

    public DataModelList toList() {
        if (!isList() || !(this instanceof DataModelList)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (DataModelList)this;
    }

    public boolean isPrimitive() {
        return type == DataModelType.PRIMITIVE;
    }

    public DataModelPrimitive<?> toPrimitive() {
        if (!isPrimitive() || !(this instanceof DataModelPrimitive)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (DataModelPrimitive)this;
    }

    public abstract Object toStorageObject();

    private final DataModelType type;

    public static DataModelValue fromObject(Object ob) {
        if (ob instanceof DataModelValue) {
            return (DataModelValue)ob;
        }
        if (ob instanceof Map) {
            return new DataModelMap((Map)ob);
        }
        if (ob instanceof List) {
            return new DataModelList((List)ob);
        }
        if (ob == null || ob instanceof Void) {
            return new DataModelPrimitive<Void>(null);
        }
        if (ob instanceof String) {
            return new DataModelPrimitive<>((String) ob);
        }
        if (ob instanceof Integer) {
            return new DataModelPrimitive<>((Integer) ob);
        }
        if (ob instanceof Float) {
            return new DataModelPrimitive<>((Float) ob);
        }
        if (ob instanceof Boolean) {
            return new DataModelPrimitive<>((Boolean) ob);
        }
        return new DataModelPrimitive<Void>(null);
    }
}
