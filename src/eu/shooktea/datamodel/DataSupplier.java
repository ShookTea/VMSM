package eu.shooktea.datamodel;

import java.io.*;
import java.util.Scanner;
import java.util.function.Function;

public interface DataSupplier {
    DataModelMap load(String s);
    String store(DataModelMap dmm);
    Function<Object, DataModelValue> converter();

    Function<Object, DataModelValue> genericConverter = ob -> {
        if (ob instanceof String) {
            return new DataModelPrimitive<>((String) ob);
        }
        if (ob instanceof Integer) {
            return new DataModelPrimitive<>((Integer) ob);
        }
        if (ob instanceof Float) {
            return new DataModelPrimitive<>((Float) ob);
        }
        if (ob instanceof Boolean) {
            return new DataModelPrimitive<>((Boolean) ob);
        }
        return new DataModelPrimitive<Void>(null);
    };

    default DataModelMap load(InputStream is) {
        StringBuilder sb = new StringBuilder();
        Scanner sc = new Scanner(is);
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine()).append(System.lineSeparator());
        }
        sc.close();
        return load(sb.toString());
    }

    default DataModelMap load(Reader r) {
        StringBuilder sb = new StringBuilder();
        Scanner sc = new Scanner(r);
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine()).append(System.lineSeparator());
        }
        sc.close();
        return load(sb.toString());
    }

    default DataModelMap load(File f) {
        try {
            return load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    default String store(DataModelMap dmm, Writer w) {
        try {
            String text = store(dmm);
            w.append(text);
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default String store(DataModelMap dmm, OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        String text = store(dmm);
        pw.println(text);
        pw.close();
        return text;
    }

    default String store(DataModelMap dmm, File f) {
        try {
            return store(dmm, new FileOutputStream(f));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
