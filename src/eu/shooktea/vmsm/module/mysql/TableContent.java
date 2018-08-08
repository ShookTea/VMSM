package eu.shooktea.vmsm.module.mysql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TableContent {
    public TableContent(ResultSet set) throws SQLException {
        ResultSetMetaData metaData = set.getMetaData();
        int count = metaData.getColumnCount();
        this.columnNames = new String[count];
        for (int i = 0; i < count; i++) {
            columnNames[i] = metaData.getColumnLabel(i+1);
        }

        rows = FXCollections.observableArrayList();
        while (set.next()) {
            String[] row = new String[count];
            for (int i = 0; i < count; i++) {
                row[i] = set.getString(i+1);
            }
            rows.add(new TableEntry(row));
        }
    }

    public ObservableList<TableEntry> getRows() {
        return rows;
    }

    public String getColumnNameAt(int index) {
        return columnNames[index];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    private ObservableList<TableEntry> rows;
    private String[] columnNames;
}
