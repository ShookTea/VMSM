package eu.shooktea.datamodel;

import java.util.function.Function;

public abstract class DataModelValue {
    DataModelValue(DataModelType type) {
        this.type = type;
    }

    public abstract Object toStorageObject();

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

    public <T> DataModelPrimitive<T> toPrimitive() {
        if (!isPrimitive() || !(this instanceof DataModelPrimitive)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (DataModelPrimitive<T>)this;
    }

    private final DataModelType type;

    public static void setConverter(Function<Object, DataModelValue> newConverter) {
        converter = newConverter;
    }

    public static DataModelValue fromObject(Object ob) {
        return converter.apply(ob);
    }

    private static Function<Object, DataModelValue> converter = obj -> new DataModelPrimitive<Void>(null);
}
