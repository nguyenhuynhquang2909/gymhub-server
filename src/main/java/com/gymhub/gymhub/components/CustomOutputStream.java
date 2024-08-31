package com.gymhub.gymhub.components;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class CustomOutputStream extends ObjectOutputStream{
    public CustomOutputStream(FileOutputStream fos) throws IOException {
        super(fos);
    }

    public CustomOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    protected CustomOutputStream() throws IOException, SecurityException {
    }

    @Override
    protected void writeStreamHeader() throws IOException {

    }
}
