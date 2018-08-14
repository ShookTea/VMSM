package eu.shooktea.yaml;

import java.util.ArrayList;
import java.util.List;

public class YamlList extends YamlValue {
    public YamlList(List<Object> objectList) {
        super(YamlType.LIST);
        this.list = new ArrayList<>();
        for (Object ob : objectList) {
            this.list.add(YamlValue.fromObject(ob));
        }
    }

    public YamlList() {
        this(new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("YamlList{");
        for (int i = 0; i < list.size(); i++) {
            builder.append(i == 0 ? "" : ";").append(list.get(i));
        }
        return builder.append("}").toString();
    }

    private final List<YamlValue> list;
}
