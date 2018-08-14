package eu.shooktea.yaml;

import java.util.ArrayList;
import java.util.List;

public class YamlList extends YamlValue {
    public YamlList(List<Object> objectList) {
        super(YamlType.LIST);
        this.list = objectList;
    }

    public YamlList() {
        this(new ArrayList<>());
    }

    @Override
    public String toString() {
        return "YamlList{" +
                "list=" + list +
                '}';
    }

    private final List<Object> list;
}
