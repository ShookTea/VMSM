package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.fxtoolkit.FXToolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.VirtualMachine;
import eu.shooktea.vmsm.view.View;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.reactfx.value.Val;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

public class SimpleGuiController {
    private SimpleGuiController() {}

    private static Label mainButton;
    private static Pane root;
    private static QuickGui quickGui;

    public static void init(Label label, Pane p) {
        VM.addListener(() -> VM.ifNotNull(vm -> titleStringProperty.setValue(vm.getName()))).vmConsume();
        Val.flatMap(VM.getProperty(), VirtualMachine::statusProperty).filter(Objects::nonNull)
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) return;
                    View.showMessage(newValue.getTooltipText(), newValue.getInfoColor());
                });
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

    public static void addMessage(String message, Color backgroundColor) {
        if (message == null || message.trim().isEmpty()) return;
        if (backgroundColor == null) backgroundColor = View.defaultBackgroundColor;
        messageQueue.offer(new Pair<>(message.trim(), backgroundColor));
        Platform.runLater(SimpleGuiController::updateMessage);
    }

    private static void updateMessage() {
        if (currentMessage == null && !messageQueue.isEmpty()) {
            Pair<String,Color> pair = messageQueue.poll();
            currentMessage = pair.getKey();
            Color color = pair.getValue();
            mainButton.textProperty().unbind();
            mainButton.setText(currentMessage);
            mainButton.setBackground(createBackground(color));
            mainButton.setTextFill(calculateForeground(color));
            FXToolkit.runPlatformAfter(5000, () -> {
                currentMessage = null;
                mainButton.textProperty().bind(titleStringProperty);
                mainButton.setBackground(createBackground(View.defaultBackgroundColor));
                mainButton.setTextFill(calculateForeground(View.defaultBackgroundColor));
                updateMessage();
            });
        }
    }

    private static Background createBackground(Color color) {
        return new Background(
                new BackgroundFill(color, new CornerRadii(0.1, true), new Insets(0.0))
        );
    }

    private static Color calculateForeground(Color color) {
        double red = 255 * color.getRed();
        double green = 255 * color.getGreen();
        double blue = 255 * color.getBlue();
        double value = red * 0.299 + green * 0.587 + blue * 0.114;
        return value > 186 ? Color.BLACK : Color.WHITE;
    }

    public static void setTitle(String title) {
        titleStringProperty.setValue(title);
    }

    private static StringProperty titleStringProperty = new SimpleStringProperty("VMSM");
    private static String currentMessage = null;
    private static Queue<Pair<String,Color>> messageQueue = new ArrayDeque<>();
    private static ContextMenu menu = null;
}
