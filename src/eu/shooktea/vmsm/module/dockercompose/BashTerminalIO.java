package eu.shooktea.vmsm.module.dockercompose;

import eu.shooktea.fxtoolkit.terminal.TerminalIO;

import java.io.*;

public class BashTerminalIO implements TerminalIO {

    public BashTerminalIO(String serviceName, File directory) {
        builder = new ProcessBuilder("docker-compose", "exec", serviceName, "bash").directory(directory);
    }

    @Override
    public void init() {
        try {
            Process process = builder.start();
            inputStream = process.getOutputStream();
            new Thread(()->{
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {}

    @Override
    public void setInputStream(OutputStream stream) {
        this.outputStream = stream;
    }

    @Override
    public OutputStream getTerminalOutputStream() {
        return outputStream;
    }

    @Override
    public OutputStream getTerminalInputStream() {
        return inputStream;
    }

    private final ProcessBuilder builder;
    private OutputStream outputStream = null;
    private OutputStream inputStream = null;
}
