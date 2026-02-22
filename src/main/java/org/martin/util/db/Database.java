package org.martin.util.db;

import org.martin.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.martin.util.db.FileUtil.readFile;

public class Database {

    public static final String dbName = "mainDelta.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:"+dbName);
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

        String pragmaSchema = readFile("/sql/pragma.sql");
        executeSQL(pragmaSchema);

        String blogSchema = readFile("/sql/schema/blogSchema.sql");
        String userSchema = readFile("/sql/schema/userSchema.sql");
        String sessionSchema = readFile("/sql/schema/userSessions.sql");
        String userExtraSchema = readFile("/sql/schema/userExtras.sql");
        String readBlogSchema = readFile("/sql/schema/readBlogsSchema.sql");
        //...

        executeSQL(blogSchema);
        executeSQL(userSchema);
        executeSQL(sessionSchema);
        executeSQL(userExtraSchema);
        executeSQL(readBlogSchema);

    }
}
