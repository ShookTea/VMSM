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

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public abstract class VMType {

    public VMType() {
        typeName = new SimpleStringProperty("typeName");
        creationInfo = new SimpleStringProperty("");
        toolBarElements = new SimpleListProperty<>();
    }

    public String getTypeName() {
        return typeName.getValue();
    }

    public ReadOnlyStringProperty typeNameProperty() {
        return typeName;
    }

    public String getCreationInfo() {
        return creationInfo.getValue();
    }

    public ReadOnlyStringProperty creationInfoProperty() {
        return creationInfo;
    }

    public String getCreationError() {
        return creationError.getValue();
    }

    public void setCreationError(String err) {
        creationError.setValue(err);
    }

    public StringProperty creationErrorProperty() {
        return creationError;
    }

    public final void checkVmRootFile(File file) {
        setCreationError(checkRootFile(file));
    }

    public ObservableList<Node> getToolBarElements() {
        return toolBarElements.getValue();
    }

    public void setToolBarElements(ObservableList<Node> list) {
        toolBarElements.setValue(list);
    }

    public ListProperty<Node> toolBarElementsProperty() {
        return toolBarElements;
    }

    /**
     * Check whether this file can be correct root file for VM.
     * @param file root file choosen by user.
     * @return empty string or {@code null} if file is correct, error information otherwise.
     */
    protected String checkRootFile(File file) {
        return "";
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    public boolean isMainPathDirectory() {
        return true;
    }

    protected ReadOnlyStringProperty typeName;
    protected ReadOnlyStringProperty creationInfo;
    protected ListProperty<Node> toolBarElements;
    private final StringProperty creationError = new SimpleStringProperty("");

    public static VMType getByName(String name) {
        return types.stream()
                .filter(type -> type.getTypeName().equals(name))
                .findAny().get();
    }

    public static final ObservableList<VMType> types = FXCollections.observableArrayList(
            new Vagrant()
    );

    protected static ImageView createToolbarImage(String resourceFileName) {
        resourceFileName = "/eu/shooktea/vmsm/resources/" + resourceFileName;
        ImageView iv = new ImageView(new Image(VMType.class.getResourceAsStream(resourceFileName)));
        iv.setPreserveRatio(true);
        iv.setFitWidth(20);
        return iv;
    }
}
