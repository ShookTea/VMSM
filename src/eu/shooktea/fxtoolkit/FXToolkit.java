package eu.shooktea.fxtoolkit;

public class FXToolkit {
    private FXToolkit() {}

    public static PlatformThreadHandler onPlatform(Runnable r) {
        return new PlatformThreadHandler(r);
    }
}
