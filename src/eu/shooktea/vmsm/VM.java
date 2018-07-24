package eu.shooktea.vmsm;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Singleton object for keeping currently displayed virtual machine.
 */
public class VM {
    private VM() {}

    /**
     * Sets currently displayed virtual machine. If new virtual machine to display is different than the one displayed
     * now, all registered listeners are called.
     * @param vm virtual machine to display or {@code null}, if no machine should be displayed now.
     * @see #addListener(VmEmptyListener)
     * @see #addListener(VmChangeListener)
     * @see #addListener(VmUpdateListener)
     */
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

    /**
     * Sets current virtual machine to {@code null}. If some virtual machine was displayed before calling this method,
     * it will call all registered listeners.
     * @see #set(VirtualMachine)
     */
    public static void unset() {
        set(null);
    }

    /**
     * Returns currently displayed virtual machine or {@code null} if no machine is displayed.
     * @return currently displayed virtual machine
     */
    public static VirtualMachine get() {
        return currentVm.get();
    }

    /**
     * Returns property containing virtual machine. Should be used only for bindings.
     * @return property with virtual machine.
     */
    public static ReadOnlyObjectProperty<VirtualMachine> getProperty() {
        return currentVm;
    }

    /**
     * Returns {@code true} if there is any displayed virtual machine, {@code false} otherwise.
     * @return {@code true} if currently displayed virtual machine is set.
     */
    public static boolean isSet() {
        return currentVm.isNotNull().get();
    }

    /**
     * Returns currently displayed virtual machine. If there is no virtual machine, instead of returning {@code} null it will
     * throw an exception.
     * @param thr exception to be thrown
     * @param <T> type of exception
     * @return currently displayed virtual machine
     * @throws T if there is no displayed virtual machine
     */
    public static <T extends Throwable> VirtualMachine getOrThrow(T thr) throws T {
        if (!isSet()) throw thr;
        return get();
    }

    /**
     * Returns currently displayed virtual machine or throws runtime exception if there isn't any displayed VM.
     * @return currently displayed virtual machine
     * @throws RuntimeException if there is no displayed virtual machine
     */
    public static VirtualMachine getOrThrow() {
        return getOrThrow(new RuntimeException("VM not found"));
    }

    /**
     * Adds new change listener.
     * @param listener new change listener
     * @return added listener
     * @see VmChangeListener
     */
    public static VmChangeListener addListener(VmChangeListener listener) {
        listeners.add(listener);
        return listener;
    }

    /**
     * Adds new update listener.
     * @param listener new update listener
     * @return added listener
     * @see VmUpdateListener
     */
    public static VmUpdateListener addListener(VmUpdateListener listener) {
        listeners.add(listener);
        return listener;
    }

    /**
     * Adds new empty listener.
     * @param listener new empty listener
     * @return added listener
     * @see VmEmptyListener
     */
    public static VmEmptyListener addListener(VmEmptyListener listener) {
        listeners.add(listener);
        return listener;
    }

    /**
     * Removes previously added listener.
     * @param listener listener to be removed
     */
    public static void removeListener(VmListener listener) {
        listeners.remove(listener);
    }

    /**
     * Calls some action if the virtual machine is setted.
     * @param consumer action to be done if VM is setted.
     */
    public static void ifNotNull(Consumer<VirtualMachine> consumer) {
        if (get() != null) consumer.accept(get());
    }

    /**
     * Returns {@code true} if given VM is currently displayed, @{code false} otherwise.
     * @param vm virtual machine to be checked
     * @return boolean
     * @see #isNotEqual(VirtualMachine)
     */
    public static boolean isEqual(VirtualMachine vm) {
        return currentVm.get() == vm;
    }

    /**
     * Returns {@code false} if given VM is currently displayed, @{code true} otherwise.
     * @param vm virtual machine to be checked
     * @return boolean
     * @see #isEqual(VirtualMachine)
     */
    public static boolean isNotEqual(VirtualMachine vm) {
        return !isEqual(vm);
    }

    private static ObjectProperty<VirtualMachine> currentVm = new SimpleObjectProperty<>(null);
    private static List<VmListener> listeners = new ArrayList<>();

    /**
     * This listener is called whenever virtual machine to be displayed is changed. It accepts two arguments: previously
     * displayed machine and currently displayed machine.
     */
    public interface VmChangeListener extends VmListener {
        public void vmChanged(VirtualMachine prevVm, VirtualMachine newVm);
    }

    /**
     * This listener is called whenever virtual machine to be displayed is changed. It accepts one argument: currently
     * displayed machine.
     */
    public interface VmUpdateListener extends VmListener {
        public void vmUpdated(VirtualMachine newVm);
    }

    /**
     * This listener is called whenever virtual machine to be displayed is changed. It doesn't get any arguments.
     */
    public interface VmEmptyListener extends VmListener {
        public void vmConsume();
    }

    private interface VmListener {}
}
