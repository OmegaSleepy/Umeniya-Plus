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

import static omega.sleepy.util.FileUtil.readFile;

public class Database {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:main.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private static void executeSQL(String sql){
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.execute();
            Log.exec(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initDatabase(){
        Log.info("Database initializing...");
        String blogSchema = readFile("/sql/blogSchema.sql");
        String userSchema = readFile("/sql/userSchema.sql");
        //...

        executeSQL(blogSchema);
        executeSQL(userSchema);

    }

}
