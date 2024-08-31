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

    public static void main(String[] args) throws Exception {
        ObjectOutputStream os1 = new ObjectOutputStream(new FileOutputStream("test"));
        os1.writeObject(new CustomObjectOutputStream());
        os1.close();

        ObjectOutputStream os2 = new ObjectOutputStream(new FileOutputStream("test", true)) {
            protected void writeStreamHeader() throws IOException {
                reset();
            }
        };

        os2.writeObject(new CustomObjectOutputStream());
        os2.close();

        ObjectInputStream is = new ObjectInputStream(new FileInputStream("test"));
        System.out.println(is.readObject());
        System.out.println(is.readObject());
}}
