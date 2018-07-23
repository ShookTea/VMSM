package eu.shooktea.vmsm.view.controller;

import com.teamdev.jxbrowser.chromium.events.*;
import javafx.application.Platform;
import javafx.beans.property.*;

public class BrowserProgressBar implements LoadListener {

    @Override
    public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
        updateProgressProperty();
    }

    @Override
    public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
        updateProgressProperty();
    }

    @Override
    public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
        updateProgressProperty(finishLoadingEvent.isMainFrame());
    }

    @Override
    public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {
        updateProgressProperty();
    }

    @Override
    public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {
        updateProgressProperty();
    }

    @Override
    public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {
        updateProgressProperty();
    }

    private void updateProgressProperty() {
        updateProgressProperty(false);
    }

    private void updateProgressProperty(boolean loaded) {
        somethingHasChanged.setValue(!somethingHasChanged.get());
        Runnable r = () -> progress.setValue(loaded ? 0.0 : -1.0);
        Platform.runLater(r);
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    public ReadOnlyBooleanProperty somethingHasChangedProperty() {
        return somethingHasChanged;
    }

    private BooleanProperty somethingHasChanged = new SimpleBooleanProperty(false);
    private DoubleProperty progress = new SimpleDoubleProperty(0.0);
}
