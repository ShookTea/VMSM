package eu.shooktea.datamodel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataModelList extends DataModelValue implements List<DataModelValue> {
    public DataModelList(List<?> objectList) {
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

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public DataModelValue get(int i) {
        return list.get(i);
    }

    @Override
    public boolean add(DataModelValue val) {
        return list.add(val);
    }

    @Override
    public void add(int index, DataModelValue val) {
        list.add(index, val);
    }

    @Override
    public boolean addAll(Collection<? extends DataModelValue> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends DataModelValue> c) {
        return list.addAll(index, c);
    }

    @Override
    public DataModelValue remove(int index) {
        return list.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    public boolean remove(DataModelValue val) {
        return list.remove(val);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
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

    @Override
    public DataModelValue[] toArray() {
        return list.toArray(new DataModelValue[size()]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == this || ob == list) return true;
        if (ob == null) return false;
        if (!(ob instanceof List)) return false;
        return list.equals(ob);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public DataModelValue set(int index, DataModelValue element) {
        return list.set(index, element);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<DataModelValue> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<DataModelValue> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<DataModelValue> subList(int fromIndex, int toIndex) {
        return new DataModelList(list.subList(fromIndex, toIndex));
    }

    private final List<DataModelValue> list;
}
