package eu.shooktea.vmsm.view.controller.simplegui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Arrays;
import java.util.List;

public class QuickGuiMenu {
    QuickGuiMenu() {
        this.list = createList();
    }

    public List<ImageView> getList() {
        return list;
    }

    private List<ImageView> createList() {
        ImageView test = new ImageView(new Image(QuickGuiMenu.class.getResourceAsStream("/eu/shooktea/vmsm/resources/green_ball.png")));
        return Arrays.asList(test);
    }

    private final List<ImageView> list;
}
