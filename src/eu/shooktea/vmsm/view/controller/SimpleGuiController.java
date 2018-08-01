package eu.shooktea.vmsm.view.controller;

import eu.shooktea.vmsm.view.View;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

public class SimpleGuiController {
    private SimpleGuiController() {}

    public static void openGui(MouseEvent e) {
        MenuItem item = new MenuItem("Test");
        ContextMenu menu = new ContextMenu(item);
        menu.show(View.stage(), e.getScreenX(), e.getScreenY());
    }
}
