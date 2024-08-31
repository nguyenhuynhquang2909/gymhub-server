package com.gymhub.gymhub.in_memory;

import java.io.*;

public class CustomSerializable implements Serializable {


    public static void main(String[] args) throws Exception {
        ObjectOutputStream os1 = new ObjectOutputStream(new FileOutputStream("test"));
        os1.writeObject(new CustomSerializable());
        os1.close();

        ObjectOutputStream os2 = new ObjectOutputStream(new FileOutputStream("test", true)) {
            protected void writeStreamHeader() throws IOException {
                reset();
            }
        };

        os2.writeObject(new CustomSerializable());
        os2.close();

        ObjectInputStream is = new ObjectInputStream(new FileInputStream("test"));
        System.out.println(is.readObject());
        System.out.println(is.readObject());
    }}