package eu.shooktea.vmsm.view.controller.simplegui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

class QuickGui {
    QuickGui(Pane pane) {
        this.pane = pane;
        this.startHeight = pane.getHeight();
        this.startWidth = pane.getWidth();
    }

    void switchGui() {
        if (isShortGuiOpen) closeGui(); else openGui();
    }

    public void closeGui() {
        if (!isShortGuiOpen) return;
        pane.setPrefWidth(startWidth);
        pane.setPrefHeight(startHeight);
        addedButtons.forEach(b -> pane.getChildren().remove(b));
        addedButtons.clear();
        isShortGuiOpen = false;
    }

    private void openGui() {
        if (isShortGuiOpen) return;
        addedButtons.forEach(b -> pane.getChildren().remove(b));
        addedButtons.clear();

        pane.setPrefWidth(startWidth + DISPLAY_RADIUS);
        pane.setPrefHeight(startHeight + DISPLAY_RADIUS);

        List<ImageView> menu = new QuickGuiMenu().getList(this);
        List<Point2D> points = getPoints(menu.size());

        int size = Math.min(menu.size(), points.size());
        for (int i = 0; i < size; i++) {
            ImageView imageView = menu.get(i);
            Point2D point = points.get(i);

            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(32);

            pane.getChildren().add(imageView);
            Bounds bounds = imageView.getBoundsInParent();
            imageView.setX(point.getX() - bounds.getWidth() / 2);
            imageView.setY(point.getY() - bounds.getHeight() / 2);
            addedButtons.add(imageView);
        }

        isShortGuiOpen = true;
    }

    void mouseEntered() {
        requestedExit = false;
    }

    void mouseExited() {
        if (isShortGuiOpen) requestExit();
    }

    private void requestExit() {
        requestedExit = true;
        new Thread(() -> {
            try {
                Thread.sleep(2000); //2 seconds
            } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                if (requestedExit) {
                    requestedExit = false;
                    closeGui();
                }
            });
        }).start();
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
            double x = BUTTON_RADIUS * Math.cos(rads);
            double y = BUTTON_RADIUS * Math.sin(rads);
            points.add(new Point2D(x, y));
        }
        return points;
    }

    private final Pane pane;
    private final double startWidth;
    private final double startHeight;
    private final List<ImageView> addedButtons = new ArrayList<>();
    private boolean isShortGuiOpen = false;
    private boolean requestedExit = false;

    private static final double BUTTON_RADIUS = 150.0;
    private static final double DISPLAY_RADIUS = 200.0;
}
