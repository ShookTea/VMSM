package eu.shooktea.vmsm.view.controller.mysql;

import com.jcraft.jsch.JSchException;
import eu.shooktea.vmsm.module.MySQL;
import eu.shooktea.vmsm.module.SqlConnection;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.application.Platform;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

public class MysqlTerminal implements StageController {

    @FXML private TextArea output;
    @FXML private TextField input;

    private MySQL mysql;
    private Stage stage;
    private SqlConnection connection;
    private Console out;

    @FXML
    private void initialize() {
        out = new Console(output);

        mysql = MySQL.getModuleByName("MySQL");
        connection = mysql.createConnection();

        try {
            connection.open();
            out.println("Use input field to write MySQL queries.");
            out.println("Write \"exit\" to close terminal and \"clear\" to clear output area.");
        } catch (JSchException | SQLException e) {
            out.println(e);
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(e -> {
            if (connection != null && connection.isOpen()) {
                try {
                    connection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void enterPressed() {
        String line = input.getText();
        input.clear();
        boolean exit = line.equals("exit");
        try {
            if (exit) {
                stage.close();
                connection.close();
            }
            else if (line.equals("clear")) {
                output.clear();
            }
            else {
                Object result = connection.query(line);
                if (result instanceof ResultSet) {
                    ResultSet set = (ResultSet) result;
                    printResult(set);
                    set.close();
                }
                else {
                    Integer i = (Integer)result;
                    out.println("Query has been run correctly, " + i + " rows affected.");
                }
            }
        } catch (SQLSyntaxErrorException ex) {
            out.println(ex.getMessage());
        } catch (SQLException ex) {
            if (exit) {
                ex.printStackTrace();
                System.exit(1);
            }
            else {
                out.println(ex);
            }
        }
    }

    private void printResult(ResultSet result) throws SQLException {
        ResultSetMetaData rsmd = result.getMetaData();
        String[] headerRow = new String[rsmd.getColumnCount()];
        int[] columnWidth = new int[headerRow.length];
        for (int i = 1; i <= headerRow.length; i++) {
            headerRow[i-1] = rsmd.getColumnLabel(i);
            columnWidth[i-1] = rsmd.getColumnLabel(i).length();
        }
        int count = 0;
        while (result.next()) {
            count++;
            for (int i = 0; i < columnWidth.length; i++) {
                String value = result.getString(i + 1);
                columnWidth[i] = Math.max(columnWidth[i], value == null ? 0 : value.length());
            }
        }

        printHeader(headerRow, columnWidth, count == 0);
        if (count > 0) {
            result.beforeFirst();
            while (result.next()) {
                String[] line = new String[columnWidth.length];
                for (int i = 0; i < line.length; i++) {
                    line[i] = result.getString(i + 1);
                }
                printLine(columnWidth, line);
            }
        }
        printLine(columnWidth, '┗', '━', '┷', '┛');
    }

    private void printHeader(String[] headerRow, int[] columnWidth, boolean headerOnly) {
        printLine(columnWidth, '┏', '━', '┯', '┓');
        printLine(columnWidth, headerRow);
        if (!headerOnly)
            printLine(columnWidth, '┣', '━', '┿', '┫');
    }

    private void printLine(int[] columnWidth, char leftEnd, char center, char divide, char rightEnd) {
        StringBuilder builder = new StringBuilder();
        builder.append(leftEnd);
        for (int i = 0; i < columnWidth.length; i++) {
            for (int j = 0; j < columnWidth[i]; j++) builder.append(center);
            if (i < columnWidth.length - 1) builder.append(divide);
        }
        out.println(builder.append(rightEnd).toString());
    }

    private void printLine(int[] columnWidth, String[] elements) {
        StringBuilder builder = new StringBuilder();
        builder.append('┃');
        for (int i = 0; i < elements.length; i++) {
            builder.append(String.format("%" + columnWidth[i] + "s", elements[i]));
            if (i < elements.length - 1) builder.append('│');
        }
        out.println(builder.append('┃').toString());
    }

    public static void openMysqlTerminal(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/MysqlTerminal.fxml", "MySQL Terminal");
    }

    private class Console {
        private TextArea output;

        Console(TextArea ta) {
            this.output = ta;
        }

        public void println(String s) {
            output.appendText(s);
            output.appendText("\n");
        }

        public void println(Throwable thr) {
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            thr.printStackTrace(pw);
            println(writer.toString());
        }
    }
}
