package eu.shooktea.fxtoolkit;

import javafx.application.Platform;

public class PlatformThreadHandler {
    PlatformThreadHandler(Runnable r) {
        this.r = r;
    }

    public void run() {
        Platform.runLater(r);
    }

    public void runAfter(long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {}
            Platform.runLater(r);
        }).start();
    }

    public void runAfter(Runnable r) {
        new Thread(() -> {
            r.run();
            Platform.runLater(r);
        }).start();
    }

    private final Runnable r;
}
