package eu.shooktea.vmsm.view.controller.simplegui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RightClickMenu {
    RightClickMenu() {
        menu = createContextMenu();
    }

    public ContextMenu getContextMenu() {
        return menu;
    }

    private ContextMenu createContextMenu() {
        MenuItem item = new MenuItem("Test");
        MenuItem test = new MenuItem("Hello, better world");
        ContextMenu menu = new ContextMenu(item, test);
        return menu;
    }

    private final ContextMenu menu;
}
