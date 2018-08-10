package eu.shooktea.fxtoolkit;

import javafx.application.Platform;

public class FXToolkit {
    private FXToolkit() {}

    public static void runPlatformAfter(long millis, Runnable r) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                Platform.runLater(r);
            } catch (InterruptedException e) {}
        }).start();
    }
}
