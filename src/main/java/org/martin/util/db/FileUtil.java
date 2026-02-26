package org.martin.util.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Simple util for returning the contents of a resource file
 * **/
public class FileUtil {
    public static String readFile(String path) {
        try (InputStream is = Database.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("File not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
