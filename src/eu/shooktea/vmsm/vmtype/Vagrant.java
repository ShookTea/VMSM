/*
MIT License

Copyright (c) 2018 Norbert Kowalik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package eu.shooktea.vmsm.vmtype;

import eu.shooktea.vmsm.Start;
import eu.shooktea.vmsm.VirtualMachine;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Vagrant extends VMType {
    public Vagrant() {
        super();
        this.typeName = new SimpleStringProperty("Vagrant");
        this.creationInfo = new SimpleStringProperty("Main path contains .vagrant/machines directory.");
        this.toolBarElements = new SimpleListProperty<>(FXCollections.observableArrayList(createToolBarElements()));
    }

    private List<Node> createToolBarElements() {
        ImageView vagrantIcon = createToolbarImage("vagrant_icon.png");
        ImageView statusIcon = createToolbarImage(getStatus(Start.virtualMachineProperty.get()).getResourceName());
        return Arrays.asList(vagrantIcon, statusIcon);
    }

    @Override
    protected String checkRootFile(File file) {
        if (!file.exists()) {
            return "Path does not exist.";
        }
        if (!file.isDirectory()) {
            return "Path is not a directory.";
        }
        String notCorrect = "Path is not a correct Vagrant root directory.";
        if (file.listFiles(f -> f.getName().equals(".vagrant")).length == 0) {
            return notCorrect;
        }
        File dotVagrant = file.listFiles(f -> f.getName().equals(".vagrant"))[0];
        if (dotVagrant.listFiles(f -> f.getName().equals("machines")).length == 0) {
            return notCorrect;
        }
        return "";
    }

    public Status getStatus(VirtualMachine vm) {
        return Status.RUNNING;
    }

    public enum Status {
        RUNNING, STOPPED;

        public String getResourceName() {
            switch (this) {
                case RUNNING:
                    return "green_ball.png";
                case STOPPED:
                    return "red_ball.png";
                default:
                    return "";
            }
        }
    }
}
