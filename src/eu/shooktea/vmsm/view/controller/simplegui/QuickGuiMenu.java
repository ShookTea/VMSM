package eu.shooktea.vmsm.view.controller.simplegui;

import eu.shooktea.vmsm.Toolkit;
import eu.shooktea.vmsm.VM;
import eu.shooktea.vmsm.view.controller.NewVM;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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

    private final List<ImageView> list;
}
