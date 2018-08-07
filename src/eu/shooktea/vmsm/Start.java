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

import eu.shooktea.vmsm.module.VMModule;
import eu.shooktea.vmsm.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class for VMSM. Disables SSL certificates check, loads data from configuration file and displays main window.
 */
public class Start extends Application {

    /**
     * Main method for JavaFX application that initializes GUI. You shouldn't call it by your own.
     * @param stage primary stage
     */
    @Override
    public void start(Stage stage) {
        if (isStartCalled) return;
        isStartCalled = true;
        View.initialize(stage);
        VM.addListener((oldVM, newVM) -> {
            if (oldVM != null) for (VMModule m : oldVM.getModules()) m.afterModuleTurnedOff();
            if (newVM != null) for (VMModule m : newVM.getModules()) m.afterModuleLoaded();
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

        Storage.checkVmsmFiles();
        Toolkit.turnOffSSL();
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Storage.loadAll();
        launch(args);
    }

    private static boolean isStartCalled = false;
    private static boolean isMainCalled = false;
}
