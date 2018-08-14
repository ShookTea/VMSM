package eu.shooktea.yaml;

public abstract class YamlValue {
    public YamlValue(YamlType type) {
        this.type = type;
    }

    public YamlType getType() {
        return type;
    }

    public boolean isMap() {
        return type == YamlType.MAP;
    }

    public boolean isList() {
        return type == YamlType.LIST;
    }

    public boolean isPrimitive() {
        return type == YamlType.PRIMITIVE;
    }

    private final YamlType type;
}
