package eu.shooktea.vmsm.config;

import java.io.File;
import java.io.IOException;

abstract public class AbstractFormat {
    public abstract void load(File file) throws IOException;
    public abstract void save(File file) throws IOException;
}
