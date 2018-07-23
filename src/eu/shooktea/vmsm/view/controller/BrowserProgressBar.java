package eu.shooktea.vmsm.view.controller;

import com.teamdev.jxbrowser.chromium.events.*;
import javafx.application.Platform;
import javafx.beans.property.*;

public class BrowserProgressBar implements LoadListener {

    @Override
    public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
        addProgress();
    }

    @Override
    public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
        addProgress();
    }

    @Override
    public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
        subtractProgress();
    }

    @Override
    public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {
        resetProgress();
    }

    @Override
    public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {
        subtractProgress();
    }

    @Override
    public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {
        displayProgress();
    }

    private void addProgress() {
        if (framesLoading == 0) isMainFrameLoaded = false;
        framesLoading++;
        updateProgressProperty();
    }

    private void subtractProgress() {
        framesLoaded++;
        updateProgressProperty();
    }

    private void resetProgress() {
        isMainFrameLoaded = false;
        framesLoaded = 0;
        framesLoading = 0;
        updateProgressProperty();
    }

    private void displayProgress() {
        isMainFrameLoaded = true;
        updateProgressProperty();
    }

    private void updateProgressProperty() {
        somethingHasChanged.setValue(!somethingHasChanged.get());
        Runnable r;
        if (isMainFrameLoaded) {
            r = () -> progress.setValue(framesLoaded / framesLoading);
        }
        else if (framesLoading > 0) {
            r = () -> progress.setValue(-1);
        }
        else {
            r = () -> progress.setValue(0);
        }
        Platform.runLater(r);
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    public double getProgress() {
        return progress.get();
    }

    public ReadOnlyBooleanProperty somethingHasChangedProperty() {
        return somethingHasChanged;
    }

    private int framesLoading = 0;
    private int framesLoaded = 0;
    private boolean isMainFrameLoaded = false;
    private BooleanProperty somethingHasChanged = new SimpleBooleanProperty(false);
    private DoubleProperty progress = new SimpleDoubleProperty(0.0);
}
