package eu.shooktea.vmsm.view.controller.mysql;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.module.MySQL;
import eu.shooktea.vmsm.module.SSH;
import eu.shooktea.vmsm.module.SqlConnection;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayDeque;
import java.util.Queue;

public class MysqlTerminal implements StageController {

    @FXML private TextArea output;
    @FXML private TextField input;

    private MySQL mysql;
    private Stage stage;
    private SqlConnection connection;
    private PrintStream printStream;

    @FXML
    private void initialize() {
        Console console = new Console(output);
        printStream = new PrintStream(console, true);

        mysql = MySQL.getModuleByName("MySQL");
        connection = mysql.createConnection();

        try {
            connection.open();
            printStream.println("Use input field to write MySQL queries.");
            printStream.println("Write \"exit\" to close terminal and \"clear\" to clear output area.");
        } catch (JSchException | SQLException e) {
            e.printStackTrace(printStream);
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
                ResultSet set = connection.query(line);
                printResult(set);
                set.close();
            }
        } catch (SQLSyntaxErrorException ex) {
            printStream.println(ex.getMessage());
        } catch (SQLException ex) {
            ex.printStackTrace(exit ? System.err : printStream);
            if (exit) System.exit(1);
        }
    }

    private void printResult(ResultSet result) throws SQLException {
        ResultSetMetaData rsmd = result.getMetaData();
        String[] headerRow = new String[rsmd.getColumnCount()];
        for (int i = 1; i <= headerRow.length; i++) {
            headerRow[i-1] = rsmd.getColumnLabel(i);
        }

        for (String s : headerRow) {
            printStream.println(s + " ~~~~ ");
        }
    }

    public static void openMysqlTerminal(Object... lambdaArgs) {
        View.createNewWindow("/eu/shooktea/vmsm/view/fxml/mysql/MysqlTerminal.fxml", "MySQL Terminal", false);
    }

    private class Console extends OutputStream {
        private TextArea output;

        Console(TextArea ta) {
            this.output = ta;
        }

        public void write(int i) {
            Platform.runLater(() -> {
                output.appendText(String.valueOf((char)i));
            });
        }
    }
}
