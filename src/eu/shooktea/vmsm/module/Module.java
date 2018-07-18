package eu.shooktea.vmsm.module;

import eu.shooktea.vmsm.VirtualMachine;

public abstract class Module {
    public abstract String getName();
    public abstract String getDescription();

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Module) {
            return this.getClass().getName().equals(ob.getClass().getName());
        }
        return false;
    }

    public boolean isInstalled(VirtualMachine vm) {
        return vm.getModules().contains(this);
    }
}
