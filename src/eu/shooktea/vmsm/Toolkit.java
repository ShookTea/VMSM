package eu.shooktea.vmsm;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Toolkit {
    private Toolkit() {}

    public static ImageView createToolbarImage(String resourceFileName) {
        resourceFileName = "/eu/shooktea/vmsm/resources/" + resourceFileName;
        ImageView iv = new ImageView(new Image(Toolkit.class.getResourceAsStream(resourceFileName)));
        iv.setPreserveRatio(true);
        iv.setFitWidth(20);
        return iv;
    }

    public static ImageView createMenuImage(String resourceFileName) {
        ImageView iv = createToolbarImage(resourceFileName);
        iv.setFitWidth(15);
        return iv;
    }
}
