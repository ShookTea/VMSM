package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.reactfx.value.Val;

import java.util.ArrayDeque;
import java.util.Queue;

public class SimpleGuiController {
    private SimpleGuiController() {}

    private static Label mainButton;
    private static Pane root;
    private static QuickGui quickGui;

    public static void init(Label label, Pane p) {
        VM.addListener(() -> VM.ifNotNull(vm -> titleStringProperty.setValue(vm.getName()))).vmConsume();
        Val.flatMap(VM.getProperty(), VirtualMachine::statusProperty)
                .addListener((observable, oldValue, newValue) -> View.showMessage(newValue.getTooltipText()));
        mainButton = label;
        mainButton.setPickOnBounds(true);
        mainButton.textProperty().bind(titleStringProperty);
        root = p;
        quickGui = new QuickGui(mainButton, root);
        mainButton.setOnMouseClicked(SimpleGuiController::mainButtonClicked);
        root.setPickOnBounds(true);
        root.setOnMouseExited(e -> quickGui.mouseExited());
        root.setOnMouseEntered(e -> quickGui.mouseEntered());
    }

    private static void mainButtonClicked(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (menu != null) {
                menu.hide();
                menu = null;
            }
            quickGui.switchGui();
        }
        else {
            quickGui.closeGui();
            if (menu != null) {
                menu.hide();
                menu = null;
            }
            else {
                menu = new RightClickMenu().getContextMenu();
                menu.show(root, e.getScreenX(), e.getScreenY());
            }
        }
    }

    public static void addMessage(String message) {
        if (message == null || message.trim().isEmpty()) return;
        messageQueue.offer(message.trim());
        Platform.runLater(SimpleGuiController::updateMessage);
    }

    private static void updateMessage() {
        if (currentMessage == null && !messageQueue.isEmpty()) {
            currentMessage = messageQueue.poll();
            mainButton.textProperty().unbind();
            mainButton.setText(currentMessage);
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(() -> {
                        currentMessage = null;
                        mainButton.textProperty().bind(titleStringProperty);
                        updateMessage();
                    });
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }

    public static void setTitle(String title) {
        titleStringProperty.setValue(title);
    }

    private static StringProperty titleStringProperty = new SimpleStringProperty("VMSM");
    private static String currentMessage = null;
    private static Queue<String> messageQueue = new ArrayDeque<>();
    private static ContextMenu menu = null;
}
