package eu.shooktea.vmsm.view.controller.simplegui;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayDeque;
import java.util.Queue;

public class SimpleGuiController {
    private SimpleGuiController() {}

    private static ImageView mainButton;
    private static Pane root;
    private static QuickGui quickGui;

    public static void init(ImageView iv, Pane p) {
        mainButton = iv;
        root = p;
        quickGui = new QuickGui(root);
        mainButton.setOnMouseClicked(SimpleGuiController::mainButtonClicked);
        root.setPickOnBounds(true);
        root.setOnMouseExited(e -> quickGui.mouseExited());
        root.setOnMouseEntered(e -> quickGui.mouseEntered());
    }

    private static void mainButtonClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY)
            quickGui.switchGui();
        else
            new RightClickMenu().getContextMenu().show(root, e.getScreenX(), e.getScreenY());
    }

    public static void addMessage(String message) {
        messageQueue.offer(message);
        updateMessage();
    }

    private static void updateMessage() {
        if (currentMessage != null) {
            currentMessage = null;
            quickGui.hideMessage();
        }
        if (!messageQueue.isEmpty()) {
            currentMessage = messageQueue.poll();
            quickGui.showMessage(currentMessage);
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(SimpleGuiController::updateMessage);
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }

    private static String currentMessage = null;
    private static Queue<String> messageQueue = new ArrayDeque<>();
}
