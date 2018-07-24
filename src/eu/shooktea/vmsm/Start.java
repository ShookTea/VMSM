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

import eu.shooktea.vmsm.module.Module;
import eu.shooktea.vmsm.view.controller.MainView;
import eu.shooktea.vmsm.view.controller.MainWindow;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

/**
 * Main class for VMSM. Disables SSL certificates check, loads data from configuration file and displays main window.
 */
public class Start extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MainView.initialize(stage);
        primaryStage = MainView.getMainWindowStage();
        mainWindow = MainView.getMainWindowController();
        ChangeListener<VirtualMachine> cl = ((observable, oldValue, newValue) -> {
            if (oldValue != null) for (Module m : oldValue.getModules()) m.afterModuleTurnedOff();
            if (newValue != null) for (Module m : newValue.getModules()) m.afterModuleLoaded();
            if (virtualMachineProperty.isNotNull().get()) virtualMachineProperty.get().update();
            Storage.saveAll();
        });
        Start.virtualMachineProperty.addListener(cl);
        cl.changed(virtualMachineProperty, null, virtualMachineProperty.get());
    }

    public static Stage primaryStage;
    public static MainWindow mainWindow;

    public static void main(String[] args) {
        Toolkit.turnOffSSL();
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Storage.loadAll();
        launch(args);
    }

    public static ObjectProperty<VirtualMachine> virtualMachineProperty = new SimpleObjectProperty<>();
}
