package eu.shooktea.vmsm.view.controller;

import javafx.scene.input.MouseEvent;

public class SimpleGuiController {
    private SimpleGuiController() {}

    public static void openGui(MouseEvent e) {
        System.out.println("Clicked!");
    }
}
