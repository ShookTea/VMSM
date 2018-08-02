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

    private static final double radius = 150.0;
    private static boolean isShortGuiOpen = false;

    private static ImageView mainButton;
    private static Pane root;

    public static void init(ImageView iv, Pane p) {
        mainButton = iv;
        root = p;
        mainButton.setOnMouseClicked(SimpleGuiController::mainButtonClicked);
    }

    public static void mainButtonClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY)
            switchQuickMenu();
        /*
        if (e.getButton() == MouseButton.PRIMARY)
            switchQuickMenu(e.getScreenX(), e.getScreenY());
        else
            createMenu().show(View.stage(), e.getScreenX(), e.getScreenY());
        */
    }

    private static void openGui() {
        if (isShortGuiOpen) return;
        root.setPrefWidth(root.getWidth() + radius);
        root.setPrefHeight(root.getHeight() + radius);
        isShortGuiOpen = true;
    }

    private static void closeGui() {
        if (!isShortGuiOpen) return;
        root.setPrefWidth(root.getWidth() - radius);
        root.setPrefHeight(root.getHeight() - radius);
        isShortGuiOpen = false;
    }

    private static void switchQuickMenu() {
        if (isShortGuiOpen) closeGui(); else openGui();
        if (isShortGuiOpen) {
            List<Point2D> points = getPoints(10);
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
            }
        }
    }

    private static List<Point2D> getPoints(int amountOfPoints) {
        double startAngle = 5;
        double stopAngle = 90;
        double difference = Math.abs(startAngle - stopAngle);
        difference /= amountOfPoints;

        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < amountOfPoints; i++) {
            double angle = startAngle + difference * i;
            double rads = Math.toRadians(angle);
            double x = radius * Math.cos(rads);
            double y = radius * Math.sin(rads);
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
