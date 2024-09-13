package com.gymhub.gymhub.in_memory;

import org.springframework.stereotype.Component;

import java.io.*;
@Component
public class CustomObjectOutputStream extends ObjectOutputStream {


    public CustomObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    protected CustomObjectOutputStream() throws IOException, SecurityException {
    }

}
