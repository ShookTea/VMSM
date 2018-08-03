package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.view.View;
import eu.shooktea.vmsm.view.controller.NewVM;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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
        list.add(createExitButton());

        VM.ifNotNullOrElse(
                vm -> list.addAll(vm.getQuickMenuItems()),
                () -> list.add(createNewVmButton())
        );

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

    public static ImageView toWebPage(URL url, String fileName, String tooltip) {
        ImageView iv = Toolkit.createQuickGuiButton(fileName, tooltip);
        iv.setOnMouseClicked(e -> View.openURL(url));
        return iv;
    }

    private final List<ImageView> list;
}
