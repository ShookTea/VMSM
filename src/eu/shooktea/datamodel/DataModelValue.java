package eu.shooktea.datamodel;

public interface DataModelValue {

    Object toStorageObject();
    DataModelType getType();

    default boolean isMap() {
        return getType() == DataModelType.MAP;
    }

    default DataModelMap toMap() {
        if (!isMap() || !(this instanceof DataModelMap)) throw new RuntimeException("Value " + this + " is not a map!");
        return (DataModelMap)this;
    }

    default boolean isList() {
        return getType() == DataModelType.LIST;
    }

    default DataModelList toList() {
        if (!isList() || !(this instanceof DataModelList)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (DataModelList)this;
    }

    default boolean isPrimitive() {
        return getType() == DataModelType.PRIMITIVE;
    }

    default <T> DataModelPrimitive<T> toPrimitive() {
        if (!isPrimitive() || !(this instanceof DataModelPrimitive)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (DataModelPrimitive<T>)this;
    }
}
