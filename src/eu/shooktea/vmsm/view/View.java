package eu.shooktea.vmsm.view;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.controller.MainWindow;
import eu.shooktea.vmsm.view.controller.SimpleGuiController;
import eu.shooktea.vmsm.view.controller.StageController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class View {
    private View() {}

    public static void initialize(Stage stage) throws Exception {
        boolean isSimpleGui = Start.streamArgs().anyMatch(s -> s.equals("--simple-gui"));
        primaryStage = stage;
        if (isSimpleGui)
            initializeSimpleGui();
        else {
            initializePrimaryStage();
            initializeApplicationLoop();
        }
    }

    private static void initializeSimpleGui() {
        boolean debug = Start.streamArgs().anyMatch(s -> s.equals("--debug"));
        System.out.println("DEBUG = " + debug);
        stage().initStyle(StageStyle.TRANSPARENT);
        stage().setAlwaysOnTop(true);
        Image logo = new Image(View.class.getResourceAsStream("/eu/shooktea/vmsm/resources/logo.png"));
        ImageView view = new ImageView(logo);
        view.setPickOnBounds(true);
        view.setPreserveRatio(true);
        view.setFitWidth(32.0);
        Pane guiPane = new Pane(view);
        if (!debug) guiPane.setBackground(Background.EMPTY);
        Scene scene = new Scene(guiPane);
        if (!debug) scene.setFill(Color.TRANSPARENT);
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

    private static void initializePrimaryStage() throws Exception {
        URL location = View.class.getResource("/eu/shooktea/vmsm/view/fxml/MainWindow.fxml");
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
                    VM.ifNotNull(VirtualMachine::update);
                    controller().reloadGUI();
                }),
                new KeyFrame(Duration.seconds(5))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public static Stage stage() {
        return primaryStage;
    }

    public static MainWindow controller() {
        return mainWindow;
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

    public static void reloadGUI() {
        if (mainWindow != null) mainWindow.reloadGUI();
    }

    private static Stage primaryStage;
    private static MainWindow mainWindow;
}
