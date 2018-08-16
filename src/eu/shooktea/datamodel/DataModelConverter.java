package eu.shooktea.datamodel;

import java.util.function.Function;

public class DataModelConverter {
    private DataModelConverter() {}

    public static DataModelValue convert(Object ob) {
        return converter.apply(ob);
    }

    public static void setConverter(Function<Object, DataModelValue> converter) {
        DataModelConverter.converter = converter;
    }

    public static Function<Object, DataModelValue> getConverter() {
        return converter;
    }

    private static Function<Object, DataModelValue> converter = null;
}
