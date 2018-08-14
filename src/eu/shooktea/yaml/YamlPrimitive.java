package eu.shooktea.yaml;

public class YamlPrimitive<T> extends YamlValue {
    public YamlPrimitive(T object) {
        super(YamlType.PRIMITIVE);
        this.content = object;
    }

    @Override
    public String toString() {
        return "YamlPrimitive{" + content.toString() + "}";
    }

    @Override
    public Object toYamlObject() {
        return content;
    }

    private final T content;
}
