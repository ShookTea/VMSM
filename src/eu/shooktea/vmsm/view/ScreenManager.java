package eu.shooktea.vmsm.view;

import eu.shooktea.vmsm.Storage;
import javafx.collections.ObservableList;
import javafx.stage.Screen;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private ScreenManager() {}

    public static Screen getCurrentScreen() {
        Screen val = converter.fromString((String) Storage.config.get("screen"));
        if (val == null) val = screens.get(0);
        return val;
    }

    public static void setScreen(Screen screen) {
        Storage.config.put("screen", converter.toString(screen));
    }

    public static final ObservableList<Screen> screens = Screen.getScreens();
    private static final Map<String, Screen> screenMap = new HashMap();

    public static final StringConverter<Screen> converter = new StringConverter<Screen>() {
        @Override
        public String toString(Screen object) {
            return "Screen " + (screens.indexOf(object) + 1);
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
