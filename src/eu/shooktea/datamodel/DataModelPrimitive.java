package eu.shooktea.datamodel;

public class DataModelPrimitive<T> implements DataModelValue {
    public DataModelPrimitive(T object) {
        this.content = object;
    }

    @Override
    public DataModelType getType() {
        return DataModelType.PRIMITIVE;
    }

    @Override
    public String toString() {
        return "DataModelPrimitive{" + (content == null ? "null" : content.toString()) + "}";
    }

    @Override
    public Object toStorageObject() {
        return content;
    }

    public T getContent() {
        return content;
    }

    private final T content;
}
