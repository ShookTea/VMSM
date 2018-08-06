package eu.shooktea.vmsm;

import javafx.scene.paint.Color;

/**
 * Representation of current status of VM. These statuses are independent from type of virtual machine.
 */
public enum Status {
    /**
     * VM is turned on. It can either mean that it was on when VMSM was launched, it was turned on manually or VMSM has
     * finished turning it on.
     * <p>
     * If VM was turned on manually (before or after VMSM was launched), VM status will be marked as RUNNING when it's
     * type object recognizes its state as running, which is done in loop every five seconds. Being in RUNNING state
     * doesn't mean it is fully prepared for work; i.e. turning on Vagrant machine with Apache can take several dozens
     * of seconds, but {@code vagrant status} command will return state "on" right after first few seconds even though
     * VM is not yet ready.
     * <p>
     * If VM was turned on via VMSM interface, it is required that VM Type object that handles switching states of
     * VM will set VM's state to RUNNING in the moment VM is fully initialized and ready to work. It needs to use
     * some kind of lock in procedure of checking VM's state to assure that VMSM's 5-second loop will not change
     * displayed state of VM during loading of VM.
     */
    RUNNING,
    /**
     * VM is turned off. It can either mean that it was off when VMSM was launched, it was turned off manually or VMSM
     * has finished turning it off.
     * <p>
     * If VM was turned off manually (before or after VMSM was launched), VM status will be marked as STOPPED when it's
     * type object recognizes its state as stooped, which is done in loop every five seconds. Being in STOPPED state
     * doesn't mean that process of turning off is finished.
     * <p>
     * If VM was turned off via VMSM interface, it is required that VM Type object that handles switching states of
     * VM will set VM's state to STOPPED in the moment process of turning machine off is finished. It needs to use
     * some kind of lock in procedure of checking VM's state to assure that VMSM's 5-second loop will not change
     * displayed state of VM during loading of VM.
     */
    STOPPED,
    /**
     * VM's state is undefined. It can either mean that VMSM haven't yet checked state or it's switching VM on/off.
     * <p>
     * First case when VM's state is undefined is when VMSM has just launched or switched virtual machines. VM state
     * is then marked as UNDEFINED until it's actual state is loaded by VM type object.
     * <p>
     * Second case when VM's state is undefined is when VM type object is currently switching VM on or off. During that
     * process VM type object should set VM state to UNDEFINED and lock any changes to that state by VMSM 5-second loop.
     */
    UNDEFINED;

    /**
     * Returns name of resource icon file. For name {@code X.png} whole resource file name will be
     * {@code /eu/shooktea/vmsm/resources/X.png}. Resource name will be used to load icon representing current state
     * of virtual machine. That icon is then displayed in quick menu and can be used to switch VM on/off.
     * @return name of resource icon file
     */
    public String getResourceName() {
        switch (this) {
            case RUNNING:   return "green_ball.png";
            case STOPPED:   return "red_ball.png";
            case UNDEFINED: return "yellow_ball.png";
            default: throw new RuntimeException();
        }
    }

    /**
     * Returns tooltip text. That text is displayed in tooltip after hovering mouse over state icon.
     * @return
     */
    public String getTooltipText() {
        switch (this) {
            case RUNNING:   return "VM is on.";
            case STOPPED:   return "VM is off.";
            case UNDEFINED: return null;
            default: throw new RuntimeException();
        }
    }

    public Color getInfoColor() {
        switch (this) {
            case RUNNING:   return Color.GREEN;
            case STOPPED:   return Color.RED;
            case UNDEFINED: return null;
            default: throw new RuntimeException();
        }
    }
}
