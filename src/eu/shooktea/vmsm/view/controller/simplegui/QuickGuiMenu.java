package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.Toolkit;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class QuickGuiMenu {
    QuickGuiMenu() {
        this.list = createList();
    }

    public List<ImageView> getList() {
        return list;
    }

    private List<ImageView> createList() {
        List<ImageView> list = new ArrayList<>();
        list.add(createExitButton());
        return list;
    }

    private ImageView createExitButton() {
        ImageView exitButton = Toolkit.createQuickGuiButton("red_ball.png", "Exit VMSM");
        exitButton.setOnMouseClicked(e -> System.exit(1));
        return exitButton;
    }

    private final List<ImageView> list;
}
