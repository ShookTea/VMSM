package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.view.View;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class SimpleGuiController {
    private SimpleGuiController() {}

    private static final double radius = 100.0;
    private static boolean isShortGuiOpen = false;

    public static void openGui(MouseEvent e) {
        Pane pane = (Pane)View.stage().getScene().getRoot();
        pane.setPrefWidth(pane.getWidth() + (isShortGuiOpen ? -radius : radius));
        isShortGuiOpen = !isShortGuiOpen;
        /*
        if (e.getButton() == MouseButton.PRIMARY)
            openQuickMenu(e.getScreenX(), e.getScreenY());
        else
            createMenu().show(View.stage(), e.getScreenX(), e.getScreenY());
        */
    }

    private static void openQuickMenu(double x, double y) {
        double radius = 100;
        x += View.stage().getScene().getWidth() / 2;
        y += View.stage().getScene().getHeight() / 2;
        List<Point2D> points = getPoints(x, y, radius, 10);
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
            VBox box = new VBox(imageView);
            box.setBackground(Background.EMPTY);
            Scene scene = new Scene(box);
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.show();
            scene.getWindow().setX(point.getX() - box.getWidth() / 2);
            scene.getWindow().setY(point.getY() - box.getHeight() / 2);
        }
    }

    private static List<Point2D> getPoints(double centerX, double centerY, double radius, int amountOfPoints) {
        double startAngle = 0;
        double stopAngle = 120;
        double difference = Math.abs(startAngle - stopAngle);
        difference /= amountOfPoints;

        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < amountOfPoints; i++) {
            double angle = startAngle + difference * i;
            double rads = Math.toRadians(angle);
            double x = centerX + radius * Math.cos(rads);
            double y = centerY + radius * Math.sin(rads);
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
