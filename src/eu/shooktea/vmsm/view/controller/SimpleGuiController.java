package eu.shooktea.vmsm.view.controller;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class SimpleGuiController {
    private SimpleGuiController() {}

    private static final double BUTTON_RADIUS = 150.0;
    private static final double DISPLAY_RADIUS = 200.0;
    private static boolean isShortGuiOpen = false;
    private static int buttonsToDisplay = 1;

    private static ImageView mainButton;
    private static Pane root;
    private static double startWidth;
    private static double startHeight;

    public static void init(ImageView iv, Pane p) {
        mainButton = iv;
        root = p;
        startWidth = root.getWidth();
        startHeight = root.getHeight();
        mainButton.setOnMouseClicked(SimpleGuiController::mainButtonClicked);
    }

    public static void mainButtonClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && isShortGuiOpen)
            closeQuickGui();
        else if (e.getButton() == MouseButton.PRIMARY)
            openQuickGui();
        /*
        if (e.getButton() == MouseButton.PRIMARY)
            switchQuickMenu(e.getScreenX(), e.getScreenY());
        else
            createMenu().show(View.stage(), e.getScreenX(), e.getScreenY());
        */
    }

    private static void openQuickGui() {
        if (isShortGuiOpen) return;
        addedButtons.forEach(b -> root.getChildren().remove(b));
        addedButtons.clear();

        root.setPrefWidth(startWidth + DISPLAY_RADIUS);
        root.setPrefHeight(startHeight + DISPLAY_RADIUS);

        List<Point2D> points = getPoints(buttonsToDisplay);
        buttonsToDisplay++;
        int count = 0;
        for (Point2D point : points) {
            String url;
            switch (count) {
                case 0: url = "red"; break;
                case 1: url = "yellow"; break;
                default: url = "green";
            }
            url = "/eu/shooktea/vmsm/resources/" + url + "_ball.png";
            count++;
            ImageView imageView = new ImageView(new Image(SimpleGuiController.class.getResourceAsStream(url)));
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(32);

            root.getChildren().add(imageView);
            Bounds bounds = imageView.getBoundsInParent();
            imageView.setX(point.getX() - bounds.getWidth() / 2);
            imageView.setY(point.getY() - bounds.getHeight() / 2);
            addedButtons.add(imageView);
        }

        isShortGuiOpen = true;
    }

    private static void closeQuickGui() {
        if (!isShortGuiOpen) return;
        root.setPrefWidth(startWidth);
        root.setPrefHeight(startHeight);
        addedButtons.forEach(b -> root.getChildren().remove(b));
        addedButtons.clear();
        isShortGuiOpen = false;
    }

    private static List<ImageView> addedButtons = new ArrayList<>();


    private static List<Point2D> getPoints(int amountOfPoints) {
        double startAngle = 5;
        double stopAngle = 90;
        double difference = Math.abs(startAngle - stopAngle);
        difference /= amountOfPoints;

        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < amountOfPoints; i++) {
            double angle = startAngle + difference * i;
            double rads = Math.toRadians(angle);
            double x = BUTTON_RADIUS * Math.cos(rads);
            double y = BUTTON_RADIUS * Math.sin(rads);
            points.add(new Point2D(x, y));
        }
        return points;
    }

    private static ContextMenu createMenu() {
        MenuItem item = new MenuItem("Test");
        ContextMenu menu = new ContextMenu(item);
        return menu;
    }
}
