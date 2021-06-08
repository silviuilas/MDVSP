package ro.uaic.info.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomLogs {
    String values;

    public CustomLogs(String values) {
        this.values = values;
    }

    public void saveToMemory(String name) {
        try {
            Files.write(Paths.get("src\\main\\java\\ro\\uaic\\info\\logs\\" + name + ".txt"), values.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToValue(String... params) {
        for (String s : params) {
            values = values + s + ",";
        }
        values += '\n';
    }

    public void addToValue(String valueToAdd) {
        values = values + valueToAdd;
    }
}
