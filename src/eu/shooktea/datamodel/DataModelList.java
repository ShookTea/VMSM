package eu.shooktea.datamodel;

import java.util.*;
import java.util.stream.Collectors;

public class DataModelList extends AbstractList<DataModelValue> implements DataModelValue, List<DataModelValue> {
    public DataModelList(List<?> objectList) {
        this.list = new ArrayList<>();
        for (Object ob : objectList) {
            this.list.add(DataModelConverter.convert(ob));
        }
    }

    public DataModelList() {
        this(new ArrayList<>());
    }

    @Override
    public DataModelType getType() {
        return DataModelType.LIST;
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

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public DataModelValue get(int i) {
        return list.get(i);
    }

    public boolean remove(DataModelValue val) {
        return list.remove(val);
    }

    @Override
    public DataModelValue set(int index, DataModelValue element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, DataModelValue element) {
        list.add(index, element);
    }

    @Override
    public DataModelValue remove(int index) {
        return list.remove(index);
    }

    @Override
    public DataModelValue[] toArray() {
        return list.toArray(new DataModelValue[size()]);
    }

    private final List<DataModelValue> list;
}
