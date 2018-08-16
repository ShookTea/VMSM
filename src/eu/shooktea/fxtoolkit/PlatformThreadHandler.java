package eu.shooktea.fxtoolkit;

import javafx.application.Platform;

import java.util.function.Supplier;

public class PlatformThreadHandler {
    PlatformThreadHandler(Runnable r) {
        this.r = r;
    }

    public void run() {
        Platform.runLater(r);
    }

    public void after(long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {}
            Platform.runLater(r);
        }).start();
    }

    public void after(Runnable before) {
        new Thread(() -> {
            before.run();
            Platform.runLater(r);
        }).start();
    }

    public void afterTrying(Supplier<Boolean> endCase, long breakMillis) {
        new Thread(() -> {
            while (!endCase.get()) try {
                Thread.sleep(breakMillis);
            } catch (InterruptedException ignored) {}
            Platform.runLater(r);
        }).start();
    }

    private final Runnable r;
}
