package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.Toolkit;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class RightClickMenu {
    RightClickMenu() {
        menu = createContextMenu();
    }

    public ContextMenu getContextMenu() {
        return menu;
    }

    private ContextMenu createContextMenu() {
        ContextMenu menu = new ContextMenu(exit());
        return menu;
    }

    private MenuItem separator() {
        return new SeparatorMenuItem();
    }

    private MenuItem exit() {
        MenuItem item = new MenuItem("Exit VMSM");
        item.setGraphic(Toolkit.createMenuImage("exit.png"));
        item.setOnAction(e -> System.exit(0));
        return item;
    }

    private final ContextMenu menu;
}
