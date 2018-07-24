package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.Start;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class MainView {
    private MainView() {}

    public static void initialize(Stage stage) throws Exception {
        initializePrimaryStage(stage);
        initializeApplicationLoop();
    }

    private static void initializePrimaryStage(Stage stage) throws Exception {
        primaryStage = stage;
        URL location = MainView.class.getResource("/eu/shooktea/vmsm/view/fxml/MainWindow.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        VBox vbox = loader.load();
        mainWindow = loader.getController();
        primaryStage.setScene(new Scene(vbox));
        primaryStage.setMaximized(true);
        primaryStage.setTitle("VMSM");
        primaryStage.setOnCloseRequest(e -> mainWindow.close());
        primaryStage.show();
    }

    private static void initializeApplicationLoop() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, (ev) -> {
                    if (Start.virtualMachineProperty.isNotNull().get()) Start.virtualMachineProperty.get().update();
                    getMainWindowController().reloadGUI();
                }),
                new KeyFrame(Duration.seconds(5))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public static Stage getMainWindowStage() {
        return primaryStage;
    }

    public static MainWindow getMainWindowController() {
        return mainWindow;
    }

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

    private static Stage primaryStage;
    private static MainWindow mainWindow;
}
