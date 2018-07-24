package eu.shooktea.vmsm;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VM {
    private VM() {}

    public static void set(VirtualMachine vm) {
        if (isEqual(vm)) return;
        VirtualMachine previous = currentVm.get();
        currentVm.setValue(vm);
        for (VmListener listener : listeners) {
            if (listener instanceof VmChangeListener)
                ((VmChangeListener) listener).vmChanged(previous, vm);
            else if (listener instanceof VmUpdateListener)
                ((VmUpdateListener) listener).vmUpdated(vm);
            else if (listener instanceof VmEmptyListener)
                ((VmEmptyListener) listener).vmConsume();
        }
    }

    public static void unset() {
        set(null);
    }

    public static VirtualMachine get() {
        return currentVm.get();
    }

    public static ReadOnlyObjectProperty<VirtualMachine> getProperty() {
        return currentVm;
    }

    public static boolean isSet() {
        return currentVm.isNotNull().get();
    }

    public static <T extends Throwable> VirtualMachine getOrThrow(T thr) throws T {
        if (!isSet()) throw thr;
        return get();
    }

    public static VirtualMachine getOrThrow() {
        return getOrThrow(new RuntimeException("VM not found"));
    }

    public static VmChangeListener addListener(VmChangeListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static VmUpdateListener addListener(VmUpdateListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static VmEmptyListener addListener(VmEmptyListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static void removeListener(VmListener listener) {
        listeners.remove(listener);
    }

    public static void ifNotNull(Consumer<VirtualMachine> consumer) {
        if (get() != null) consumer.accept(get());
    }

    public static boolean isEqual(VirtualMachine vm) {
        return currentVm.get() == vm;
    }

    public static boolean isNotEqual(VirtualMachine vm) {
        return !isEqual(vm);
    }

    private static ObjectProperty<VirtualMachine> currentVm = new SimpleObjectProperty<>(null);
    private static List<VmListener> listeners = new ArrayList<>();

    public interface VmChangeListener extends VmListener {
        public void vmChanged(VirtualMachine prevVm, VirtualMachine newVm);
    }

    public interface VmUpdateListener extends VmListener {
        public void vmUpdated(VirtualMachine newVm);
    }

    public interface VmEmptyListener extends VmListener {
        public void vmConsume();
    }

    private interface VmListener {}
}
