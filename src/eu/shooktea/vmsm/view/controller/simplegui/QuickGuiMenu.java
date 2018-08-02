package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.view.controller.NewVM;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuickGuiMenu {
    QuickGuiMenu() {
        this.list = createList();
    }

    public List<ImageView> getList(QuickGui gui) {
        List<ImageView> ret = new ArrayList<>(list);
        ret.forEach(iv -> {
            EventHandler<? super MouseEvent> eh = iv.getOnMouseClicked();
            iv.setOnMouseClicked(ev -> {
                gui.closeGui();
                if (eh != null) eh.handle(ev);
            });
        });
        return ret;
    }

    private List<ImageView> createList() {
        List<ImageView> list = new ArrayList<>();

        VM.ifNotNullOrElse(
                vm -> {
                    if (vm.getPageRoot() != null) list.add(home(vm.getPageRoot()));
                },
                () -> list.add(createNewVmButton())
        );

        list.add(createExitButton());
        return list;
    }

    private ImageView createExitButton() {
        ImageView exitButton = Toolkit.createQuickGuiButton("exit.png", "Exit VMSM");
        exitButton.setOnMouseClicked(e -> System.exit(0));
        return exitButton;
    }

    private ImageView createNewVmButton() {
        ImageView exitButton = Toolkit.createQuickGuiButton("terminal.png", "Create new VM");
        exitButton.setOnMouseClicked(NewVM::openNewVmWindow);
        return exitButton;
    }

    private ImageView home(URL home) {
        return toWebPage(home, "home.png", "Go to home page");
    }

    private ImageView toWebPage(URL url, String fileName, String tooltip) {
        ImageView iv = Toolkit.createQuickGuiButton(fileName, tooltip);
        iv.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
        });
        return iv;
    }

    private final List<ImageView> list;
}
