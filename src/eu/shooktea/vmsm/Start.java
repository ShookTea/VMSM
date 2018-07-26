/*
MIT License

Copyright (c) 2018 Norbert Kowalik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package eu.shooktea.vmsm;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Main class for VMSM. Disables SSL certificates check, loads data from configuration file and displays main window.
 */
public class Start extends Application {

    /**
     * Main method for JavaFX application that initializes GUI. You shouldn't call it by your own.
     * @param stage primary stage
     * @throws Exception if anything wrong happens during initialization of GUI.
     */
    @Override
    public void start(Stage stage) throws Exception {
        if (isStartCalled) return;
        isStartCalled = true;

        View.initialize(stage);
        VM.addListener((oldVM, newVM) -> {
            if (oldVM != null) for (Module m : oldVM.getModules()) m.afterModuleTurnedOff();
            if (newVM != null) for (Module m : newVM.getModules()) m.afterModuleLoaded();
            VM.ifNotNull(VirtualMachine::update);
        }).vmChanged(null, VM.get());
        VM.addListener(Storage::saveAll);
    }

    /**
     * Main method for VMSM, called during start of application. You shouldn't call it by your own.
     * That method turns off checking SSL certificates, allows for using restricted HTTP headers, loads configuration
     * and displays VMSM window.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (isMainCalled) return;
        isMainCalled = true;
        Toolkit.turnOffSSL();
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Storage.loadAll();
        test();
        launch(args);
    }

    private static void test() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession("vagrant", "sklep.energa.dev", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("vagrant");
            session.connect();
            System.out.println("Connected");

            int assignedPort = session.setPortForwardingL(3307, "127.0.0.1", 3306);
            System.out.println("localhost: " + assignedPort);
            System.out.println("Forwarded");

            String dbUrl = "jdbc:mysql://127.0.0.1:" + assignedPort + "/energa";
            Connection conn = DriverManager.getConnection(dbUrl, "energa", "ci8Uega1");
            System.out.println("Done");
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("SELECT path, value FROM core_config_data");
            while (result.next()) {
                System.out.println(result.getString("path") + " => " + result.getString("value"));
            }
            statement.close();
            conn.close();

        } catch (Throwable thr) {
            thr.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean isStartCalled = false;
    private static boolean isMainCalled = false;
}
