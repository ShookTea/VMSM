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
package eu.shooktea.vmsm;

import eu.shooktea.vmsm.vmtype.VMType;
import javafx.beans.property.*;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class VirtualMachine {
    public VirtualMachine(String name, File mainPath, URL pageRoot, VMType type) {
        this.name = new SimpleStringProperty(name);
        this.mainPath = new SimpleObjectProperty<>(mainPath);
        this.pageRoot = new SimpleObjectProperty<>(pageRoot);
        this.type = new SimpleObjectProperty<>(type);
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name", name.get());
        obj.put("path", mainPath.get().getAbsolutePath());
        if (pageRoot.isNotNull().get()) obj.put("url", pageRoot.get().toString());
        obj.put("type", type.get().getTypeName());
        return obj;
    }

    public String getName() {
        return name.getValue();
    }

    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    public File getMainPath() {
        return mainPath.getValue();
    }

    public ObjectProperty<File> mainPathProperty() {
        return mainPath;
    }

    public URL getPageRoot() {
        return pageRoot.get();
    }

    public void setPageRoot(URL url) {
        pageRoot.set(url);
    }

    public ObjectProperty<URL> pageRootProperty() {
        return pageRoot;
    }

    public VMType getType() {
        return type.getValue();
    }

    public void setType(VMType type) {
        this.type.setValue(type);
    }

    public ObjectProperty<VMType> typeProperty() {
        return type;
    }

    public void update() {
        getType().update(this);
    }

    private ReadOnlyStringProperty name;
    private ObjectProperty<File> mainPath;
    private ObjectProperty<URL> pageRoot;
    private ObjectProperty<VMType> type;

    public static VirtualMachine fromJSON(JSONObject json) throws MalformedURLException {
        String name = json.getString("name");
        File path = new File(json.getString("path"));
        URL url = json.has("url") ? new URL(json.getString("url")) : null;
        VMType type = VMType.getByName(json.getString("type"));
        return new VirtualMachine(name, path, url, type);
    }
}
