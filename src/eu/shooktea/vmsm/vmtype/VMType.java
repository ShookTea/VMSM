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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class VMType {
    public abstract String getTypeName();

    @Override
    public String toString() {
        return getTypeName();
    }

    /**
     * Gives information about whether this VM type requires directory as it's main path. It influences
     * the input file window.
     * @return {@code true } if that VM requires directory, {@code false} if it requires file.
     */
    public boolean isMainPathDirectory() {
        return true;
    }

    public static VMType getByName(String name) {
        return types.stream()
                .filter(type -> type.getTypeName().equals(name))
                .findAny().get();
    }

    public static final ObservableList<VMType> types = FXCollections.observableArrayList(
            new Vagrant()
    );
}
