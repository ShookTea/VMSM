package eu.shooktea.vmsm;

import java.util.ArrayList;
import java.util.List;

public class VM {
    private VM() {}

    public static void set(VirtualMachine vm) {
        VirtualMachine previous = currentVm;
        currentVm = vm;
        for (VmListener listener : listeners) {
            if (listener instanceof VmChangeListener)
                ((VmChangeListener) listener).vmChanged(previous, currentVm);
            if (listener instanceof VmUpdateListener)
                ((VmUpdateListener) listener).vmUpdated(currentVm);
        }
    }

    public static void unset() {
        set(null);
    }

    public static VirtualMachine get() {
        return currentVm;
    }

    public static boolean isSet() {
        return currentVm != null;
    }

    public static <T extends Throwable> VirtualMachine getOrThrow(T thr) throws T {
        if (currentVm == null) throw thr;
        return currentVm;
    }

    public static VirtualMachine getOrThrow() {
        return getOrThrow(new RuntimeException("VM not found"));
    }

    public static void addListener(VmListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(VmListener listener) {
        listeners.remove(listener);
    }

    private static VirtualMachine currentVm = null;
    private static List<VmListener> listeners = new ArrayList<>();

    public interface VmChangeListener extends VmListener {
        public void vmChanged(VirtualMachine prevVm, VirtualMachine newVm);
    }

    public interface VmUpdateListener extends VmListener {
        public void vmUpdated(VirtualMachine newVm);
    }

    private interface VmListener {}
}
