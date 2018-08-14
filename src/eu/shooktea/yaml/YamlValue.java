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

    public YamlMap toMap() {
        if (!isMap() || !(this instanceof YamlMap)) throw new RuntimeException("Value " + this + " is not a map!");
        return (YamlMap)this;
    }

    public boolean isList() {
        return type == YamlType.LIST;
    }

    public YamlList toList() {
        if (!isList() || !(this instanceof YamlList)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (YamlList)this;
    }

    public boolean isPrimitive() {
        return type == YamlType.PRIMITIVE;
    }

    public YamlPrimitive<?> toPrimitive() {
        if (!isList() || !(this instanceof YamlPrimitive)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (YamlPrimitive)this;
    }

    private final YamlType type;
}
