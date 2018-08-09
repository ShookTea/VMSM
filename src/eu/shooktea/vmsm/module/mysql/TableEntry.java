package eu.shooktea.vmsm.module.mysql;

public class TableEntry {
    public TableEntry(String[] values) {
        this.values = values;
    }

    public String getValueAt(int index) {
        return values[index];
    }

    private final String[] values;
}
