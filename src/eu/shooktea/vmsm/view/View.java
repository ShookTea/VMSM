package eu.shooktea.vmsm.view;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.controller.simplegui.SimpleGuiController;
import eu.shooktea.vmsm.vmtype.Vagrant;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Singleton class managing displayed screens.
 */
public class View {
    private View() {}

    /**
     * Initialize view. It should be called only once, by {@link eu.shooktea.vmsm.Start#main(String[])} method.
     * @param stage primary stage
     */
    public static void initialize(Stage stage) {
        if (isInitialized) return;
        isInitialized = true;
        primaryStage = stage;
        initializeSimpleGui();
        initializeApplicationLoop();
    }

    private static boolean isInitialized = false;

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
        view.setBackground(new Background(new BackgroundFill(defaultBackgroundColor, new CornerRadii(0.1, true), new Insets(0.0))));
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
        Point2D upperLeftCorner = ScreenManager.getUpperLeftCorner();
        scene.getWindow().setX(upperLeftCorner.getX());
        scene.getWindow().setY(upperLeftCorner.getY());
        SimpleGuiController.init(view, guiPane);
        stage().show();
    }

    private static void initializeApplicationLoop() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, (ev) -> {
                    VM.ifNotNull(VirtualMachine::update);
                    checkOneMinuteLoop();
                }),
                new KeyFrame(Duration.seconds(5))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private static int oneMinuteLoop = 0;

    private static void checkOneMinuteLoop() {
        oneMinuteLoop--;
        if (oneMinuteLoop <= 0) {
            updateOneMinuteLoop();
            oneMinuteLoop = 12; //12, not 60 - checkOneMinuteLoop() is launched every 5 seconds
        }
    }

    private static void updateOneMinuteLoop() {
        Vagrant.searchUnregisteredVms();
    }

    /**
     * Returns current primary stage.
     * @return primary stage, containing quick menu button.
     */
    public static Stage stage() {
        return primaryStage;
    }

    /**
     * Creates and opens new window. Returns controller of that window. If controller class implements
     * {@link StageController}, it will sent new stage to it via {@link StageController#setStage(Stage)} method.
     *
     * @param fxmlPath full path to .fxml file
     * @param title title of newly created window
     * @param <T> root element type
     * @param <C> controller class
     * @return controller of window
     */
    public static <T extends Region, C> C createNewWindow(String fxmlPath, String title) {
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
            stage.show();
            Point2D pos = ScreenManager.getCenterPosition(stage.getWidth(), stage.getHeight());
            stage.setX(pos.getX());
            stage.setY(pos.getY());
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Opens URL in browser.
     * @param url URL to be opened
     */
    public static void openURL(URL url) {
        try {
            openURL(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Opens URL in browser.
     * @param uri URI to be opened
     */
    public static void openURL(URI uri) {
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }).start();
        }
    }

    /**
     * Displays new message with selected text and background color.
     * @param message text of message
     * @param bgColor background color
     */
    public static void showMessage(String message, Color bgColor) {
        SimpleGuiController.addMessage(message, bgColor);
    }

    /**
     * Displays new message with selected text and default background color.
     * @param message text of message
     */
    public static void showMessage(String message) {
        showMessage(message, defaultBackgroundColor);
    }

    private static Stage primaryStage;

    /**
     * Default color of the background.
     */
    public static final Color defaultBackgroundColor = Color.WHITE;
}
