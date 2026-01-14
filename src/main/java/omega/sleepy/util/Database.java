package omega.sleepy.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class Database {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:main.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static void initDatabase(){
        String blogSchema = readFile("/sql/blogSchema.sql");
        //...
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(blogSchema)){
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
