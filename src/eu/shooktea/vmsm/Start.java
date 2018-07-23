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
import eu.shooktea.vmsm.view.controller.MainWindow;
import eu.shooktea.vmsm.view.controller.StageController;
import eu.shooktea.vmsm.vmtype.VMType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

public class Start extends Application {

    public static ImageView createToolbarImage(String resourceFileName) {
        resourceFileName = "/eu/shooktea/vmsm/resources/" + resourceFileName;
        ImageView iv = new ImageView(new Image(VMType.class.getResourceAsStream(resourceFileName)));
        iv.setPreserveRatio(true);
        iv.setFitWidth(20);
        return iv;
    }

    public static ImageView createMenuImage(String resourceFileName) {
        ImageView iv = createToolbarImage(resourceFileName);
        iv.setFitWidth(15);
        return iv;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Storage.loadAll();
        Start.primaryStage = primaryStage;
        URL location = Start.class.getResource("/eu/shooktea/vmsm/view/fxml/MainWindow.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        VBox vbox = loader.load();
        mainWindow = loader.getController();
        primaryStage.setScene(new Scene(vbox));
//        primaryStage.setMaximized(true);
        primaryStage.setTitle("VMSM");
        primaryStage.setOnCloseRequest(e -> mainWindow.close());
        primaryStage.show();
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, (ev) -> {
            if (virtualMachineProperty.isNotNull().get()) virtualMachineProperty.get().update();
            mainWindow.reloadGUI();
        }), new KeyFrame(Duration.seconds(5)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        ChangeListener<VirtualMachine> cl = ((observable, oldValue, newValue) -> {
            if (oldValue != null) for (Module m : oldValue.getModules()) m.afterModuleTurnedOff();
            if (newValue != null) for (Module m : newValue.getModules()) m.afterModuleLoaded();
            if (virtualMachineProperty.isNotNull().get()) virtualMachineProperty.get().update();
            Storage.saveAll();
        });
        Start.virtualMachineProperty.addListener(cl);
        cl.changed(virtualMachineProperty, null, virtualMachineProperty.get());
    }

    private static void turnOffSSL() {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (s, sslSession) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Stage primaryStage;
    public static MainWindow mainWindow;

    public static void main(String[] args) {
        turnOffSSL();
        launch(args);
    }

    public static ObjectProperty<VirtualMachine> virtualMachineProperty = new SimpleObjectProperty<>();

    public static <T extends Region, C> C createNewWindow(String fxmlPath, String title, boolean isModal) {
        try {
            URL location = Start.class.getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(location);
            T element = loader.load();
            C controller = loader.getController();
            Stage stage = new Stage();
            if (controller instanceof StageController) {
                StageController sc = (StageController)controller;
                sc.setStage(stage);
            }
            stage.setScene(new Scene(element));
            stage.setTitle(title);
            if (isModal) {
                stage.initOwner(primaryStage);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
            else {
                stage.show();
            }
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
