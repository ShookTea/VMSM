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

import org.json.JSONObject;

import java.io.File;
import java.net.URL;

public class VirtualMachine {
    public VirtualMachine(String name, File mainPath, URL pageRoot) {
        this.name = name;
        this.mainPath = mainPath;
        this.pageRoot = pageRoot;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("path", mainPath.getAbsolutePath());
        obj.put("url", pageRoot.toString());
        return obj;
    }

    private String name;
    private File mainPath;
    private URL pageRoot;
}
