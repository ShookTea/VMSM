package eu.shooktea.vmsm.view;

import eu.shooktea.datamodel.DataModelPrimitive;
import eu.shooktea.datamodel.DataModelValue;
import eu.shooktea.vmsm.Storage;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton manager of physical screens. These screens are represented by {@link Screen} objects.
 */
public class ScreenManager {
    private ScreenManager() {}

    /**
     * Returns {@link Screen} saved in configuration that must be used by VMSM to display its content. If no screen is saved
     * or screen was removed, returns primary screen.
     * @return screen used to display VMSM content
     */
    public static Screen getCurrentScreen() {
        Screen val = null;
        Object ob = Storage.config.get("screen");
        if (ob instanceof DataModelPrimitive && ((DataModelPrimitive)ob).getContent() instanceof String) {
            DataModelPrimitive primitive = (DataModelPrimitive)Storage.config.get("screen");
            String screenName = (String)primitive.getContent();
            val = converter.fromString(screenName);
        }
        if (val == null) val = Screen.getPrimary();
        return val;
    }

    /**
     * Sets new screen in configuration file.
     * @param screen new screen.
     * @see #getCurrentScreen()
     */
    public static void setScreen(Screen screen) {
        Storage.config.put("screen", converter.toString(screen));
    }

    private static Rectangle2D getScreenBounds() {
        return getCurrentScreen().getBounds();
    }

    static Point2D getUpperLeftCorner() {
        Rectangle2D bounds = getScreenBounds();
        return new Point2D(bounds.getMinX(), bounds.getMinY());
    }

    static Point2D getCenterPosition(double width, double height) {
        Rectangle2D bounds = getScreenBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - width) / 2;
        double y = bounds.getMinY() + (bounds.getHeight() - height) / 2;
        return new Point2D(x, y);
    }

    /**
     * Observable list with all available screens
     */
    public static final ObservableList<Screen> screens = Screen.getScreens();
    private static final Map<String, Screen> screenMap = new HashMap<>();

    /**
     * Converter changing {@link Screen} object to {@link String} and vice versa.
     */
    public static final StringConverter<Screen> converter = new StringConverter<Screen>() {
        @Override
        public String toString(Screen object) {
            return "Screen " + (screens.indexOf(object) + 1) + (object == Screen.getPrimary() ? " [PRIMARY]" : "");
        }

        @Override
        public Screen fromString(String string) {
            return screenMap.get(string);
        }
    };

    static {
        for (Screen screen : screens) {
            screenMap.put(converter.toString(screen), screen);
        }
    }
}
