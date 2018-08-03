package eu.shooktea.vmsm.view;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.controller.StageController;
import eu.shooktea.vmsm.view.controller.simplegui.SimpleGuiController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class View {
    private View() {}

    public static void initialize(Stage stage) {
        primaryStage = stage;
        initializeSimpleGui();
        initializeApplicationLoop();
    }

    private static void initializeSimpleGui() {
        stage().initStyle(StageStyle.TRANSPARENT);
        stage().setAlwaysOnTop(true);
        stage().alwaysOnTopProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                stage().setAlwaysOnTop(true);
            }
        });
        stage().toFront();
        Label view = new Label("VMSM");
        view.setFont(Font.font(15));
        view.setTextFill(Color.BLACK);
        view.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0.1, true), new Insets(0.0))));
        view.setPadding(new Insets(0, 3, 0, 3));

        Pane guiPane = new Pane(view);
        guiPane.setBackground(Background.EMPTY);
        Scene scene = new Scene(guiPane);
        scene.setFill(Color.TRANSPARENT);
        guiPane.prefWidthProperty().addListener((obs, oldV, newV) -> stage().setWidth(newV.doubleValue()));
        guiPane.prefHeightProperty().addListener((obs, oldV, newV) -> stage().setHeight(newV.doubleValue()));
        stage().setScene(scene);
        stage().setResizable(false);
        stage().setTitle("VMSM");
        final double OPACITY_ON = 1.0;
        final double OPACITY_OFF = 0.25;
        scene.setOnMouseEntered(e -> stage().setOpacity(OPACITY_ON));
        scene.setOnMouseExited(e -> stage().setOpacity(OPACITY_OFF));
        stage().setOpacity(OPACITY_OFF);
        scene.getWindow().setX(0);
        scene.getWindow().setY(0);
        SimpleGuiController.init(view, guiPane);
        stage().show();
    }

    private static void initializeApplicationLoop() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, (ev) -> VM.ifNotNull(VirtualMachine::update)),
                new KeyFrame(Duration.seconds(5))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public static Stage stage() {
        return primaryStage;
    }

    public static <T extends Region, C> C createNewWindow(String fxmlPath, String title, boolean isModal) {
        try {
            URL location = View.class.getResource(fxmlPath);
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

    public static <T extends Region, C> C createNewWindow(String fxmlPath, String title) {
        return createNewWindow(fxmlPath, title, false);
    }

    public static void openURL(URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void showMessage(String message) {
        SimpleGuiController.addMessage(message);
    }

    private static Stage primaryStage;
}
