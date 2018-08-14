package eu.shooktea.datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataModelList extends DataModelValue implements Iterable<DataModelValue> {
    public DataModelList(List<Object> objectList) {
        super(DataModelType.LIST);
        this.list = new ArrayList<>();
        for (Object ob : objectList) {
            this.list.add(DataModelValue.fromObject(ob));
        }
    }

    public DataModelList() {
        this(new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("DataModelList{");
        for (int i = 0; i < list.size(); i++) {
            builder.append(i == 0 ? "" : ";").append(list.get(i));
        }
        return builder.append("}").toString();
    }

    @Override
    public Object toStorageObject() {
        return list.stream()
                .map(DataModelValue::toStorageObject)
                .collect(Collectors.toList());
    }

    public int size() {
        return list.size();
    }

    public DataModelValue get(int i) {
        return list.get(i);
    }

    public void add(DataModelValue val) {
        list.add(val);
    }

    public void add(int index, DataModelValue val) {
        list.add(index, val);
    }

    public void remove(int index) {
        list.remove(index);
    }

    public void remove(DataModelValue val) {
        list.remove(val);
    }

    public void clear() {
        list.clear();
    }

    public Stream<DataModelValue> stream() {
        return list.stream();
    }

    @Override
    public Iterator<DataModelValue> iterator() {
        return list.iterator();
    }

    private final List<DataModelValue> list;
}
