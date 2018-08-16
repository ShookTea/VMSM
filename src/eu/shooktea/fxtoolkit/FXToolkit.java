package eu.shooktea.fxtoolkit;

public class FXToolkit {
    private FXToolkit() {}

    public static PlatformThreadHandler runOnFxThread(Runnable r) {
        return new PlatformThreadHandler(r);
    }
}
