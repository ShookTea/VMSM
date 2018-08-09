package eu.shooktea.vmsm.vmtype;

import javafx.beans.property.SimpleStringProperty;

public class None extends VMType {
    None() {
        super();
        this.typeName = new SimpleStringProperty("None");
        this.creationInfo = new SimpleStringProperty("\"None\" type is used when you want to use some modules without using VM.");
        update(null);
    }

    @Override
    public String[] getModules() {
        return new String[] {
                "SSH", "MySQL"
        };
    }
}
