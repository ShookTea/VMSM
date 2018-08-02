package eu.shooktea.vmsm.view.controller.simplegui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

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
            createMenu().show(root, e.getScreenX(), e.getScreenY());
    }

    private static ContextMenu createMenu() {
        MenuItem item = new MenuItem("Test");
        ContextMenu menu = new ContextMenu(item);
        return menu;
    }
}