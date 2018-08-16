package eu.shooktea.datamodel;

public class DataModelPrimitive<T> extends DataModelValue {
    public DataModelPrimitive(T object) {
        super(DataModelType.PRIMITIVE);
        this.content = object;
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
