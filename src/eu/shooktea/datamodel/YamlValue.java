package eu.shooktea.datamodel;

import java.util.List;
import java.util.Map;

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
        if (!isPrimitive() || !(this instanceof YamlPrimitive)) throw new RuntimeException("Value " + this + " is not a primitive!");
        return (YamlPrimitive)this;
    }

    public abstract Object toYamlObject();

    private final YamlType type;

    public static YamlValue fromObject(Object ob) {
        if (ob instanceof YamlValue) {
            return (YamlValue)ob;
        }
        if (ob instanceof Map) {
            return new YamlMap((Map)ob);
        }
        if (ob instanceof List) {
            return new YamlList((List)ob);
        }
        if (ob == null || ob instanceof Void) {
            return new YamlPrimitive<Void>(null);
        }
        if (ob instanceof String) {
            return new YamlPrimitive<>((String) ob);
        }
        if (ob instanceof Integer) {
            return new YamlPrimitive<>((Integer) ob);
        }
        if (ob instanceof Float) {
            return new YamlPrimitive<>((Float) ob);
        }
        if (ob instanceof Boolean) {
            return new YamlPrimitive<>((Boolean) ob);
        }
        return new YamlPrimitive<Void>(null);
    }
}
