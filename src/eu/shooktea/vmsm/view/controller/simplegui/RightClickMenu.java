package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.Toolkit;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.List;

public class RightClickMenu {
    RightClickMenu() {
        menu = createContextMenu();
    }

    public ContextMenu getContextMenu() {
        return menu;
    }

    private ContextMenu createContextMenu() {
        List<MenuItem> list = new ArrayList<>();
        list.add(exit());

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(list);
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
