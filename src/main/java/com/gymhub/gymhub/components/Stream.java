package com.gymhub.gymhub.components;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;


@Getter
public class Stream {
    public static final String LOG_FILE_PATH = "src/main/resources/logs/cache-actions.log";
    File file = new File(LOG_FILE_PATH);
    FileInputStream fileInputStream = new FileInputStream(file);
    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
    CustomOutputStream objectOutputStream = new CustomOutputStream(fileOutputStream);
    ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(objectOutputStream);


    public Stream() throws IOException {
    }
}
