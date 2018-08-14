package eu.shooktea.yaml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YamlList extends YamlValue implements Iterable<YamlValue> {
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

    @Override
    public Object toYamlObject() {
        return list.stream()
                .map(YamlValue::toYamlObject)
                .collect(Collectors.toList());
    }

    public int size() {
        return list.size();
    }

    public YamlValue get(int i) {
        return list.get(i);
    }

    public void add(YamlValue val) {
        list.add(val);
    }

    public void add(int index, YamlValue val) {
        list.add(index, val);
    }

    public void remove(int index) {
        list.remove(index);
    }

    public void remove(YamlValue val) {
        list.remove(val);
    }

    public void clear() {
        list.clear();
    }

    public Stream<YamlValue> stream() {
        return list.stream();
    }

    @Override
    public Iterator<YamlValue> iterator() {
        return list.iterator();
    }

    private final List<YamlValue> list;
}
