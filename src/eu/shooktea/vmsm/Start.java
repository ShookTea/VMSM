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

import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Start extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Start.primaryStage = primaryStage;
        URL location = Start.class.getResource("/eu/shooktea/vmsm/view/fxml/MainWindow.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        VBox vbox = loader.load();
        primaryStage.setScene(new Scene(vbox));
        primaryStage.setMaximized(true);
        primaryStage.setTitle("VMSM");
        primaryStage.show();
    }

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public static Property<VirtualMachine> virtualMachineProperty = new SimpleObjectProperty<>();

    public static <T extends Region>void createNewWindow(String fxmlPath, String title, boolean isModal) {
        try {
            URL location = Start.class.getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(location);
            T element = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(element));
            stage.setTitle(title);
            if (isModal) {
                stage.initOwner(primaryStage);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.showAndWait();
            }
            else {
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
