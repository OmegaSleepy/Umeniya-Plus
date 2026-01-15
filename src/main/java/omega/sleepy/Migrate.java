package omega.sleepy;

import omega.sleepy.util.Log;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Migrate {
    record Blog(int id, String title, String tag, String excerpt, String content) {}

    public static void main(String[] args) throws SQLException {
//        migrateDB();
        migrateLanguage();
    }

    private static void migrateLanguage() throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:main.db");
        Map<String, String> langMap = getStringStringMap();

        for (Map.Entry<String, String> entry : langMap.entrySet()) {
            String newName = entry.getKey();
            String oldName = entry.getValue();

            String sql = "UPDATE blogs set tag = ? where tag = ?";
            try (PreparedStatement ps = db.prepareStatement(sql)) {
                ps.setString(1, newName);
                ps.setString(2, oldName);
                ps.executeUpdate();
            }
        }

        db.close();

    }

    private static @NotNull Map<String, String> getStringStringMap() {
        var newList = List.of("Математика", "Наука", "Биология", "Химия", "Физика", "Английски език", "История",
                "География", "Изкуство", "Музика", "Компютърни науки", "Икономика", "Философия",
                "Литература", "Няма", "Влякакви");
        var oldList = List.of("Mathematics", "Science", "Biology", "Chemistry", "Physics", "English", "History",
                "Geography", "Art", "Music", "Computer Science", "Economics", "Philosophy",
                "Literature", "None", "Any");

        Map<String, String> langMap =  new HashMap<>();
        for (int i = 0; i < newList.size(); i++) {
            langMap.put(newList.get(i), oldList.get(i));
        }
        return langMap;
    }

    private static void migrateDB() throws SQLException {
        Connection old_laptop = DriverManager.getConnection("jdbc:sqlite:server.db");
        Connection old_laptop2 = DriverManager.getConnection("jdbc:sqlite:old_laptop.db");
        Connection old_oc = DriverManager.getConnection("jdbc:sqlite:old_pc.db");
        Connection newOne = DriverManager.getConnection("jdbc:sqlite:main.db");

        List<Connection> connections = List.of(old_laptop, old_laptop2, old_oc);

        List<Blog> blogs = new ArrayList<>();
        for (Connection connection : connections) {
            Blog blog;
            ResultSet rs = connection.createStatement().executeQuery("SELECT * from blogs");
            while (rs.next()) {
                blog = new Blog(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("tag"),
                        rs.getString("excerpt"),
                        rs.getString("content")
                );
                blogs.add(blog);

                Log.info(blog.toString());
            }

            rs.close();
            connection.close();
        }

        for (Blog blog : blogs) {
            String insertQuery = "INSERT into blogs(title, tag, excerpt, content, creator_username, created_at)\n" +
                    "values (?, ?, ?, ?, ?, ?);";
            PreparedStatement pstmt = newOne.prepareStatement(insertQuery);
            pstmt.setString(1, blog.title);
            pstmt.setString(2, blog.tag);
            pstmt.setString(3, blog.excerpt);
            pstmt.setString(4, blog.content);
            pstmt.setString(5, "None");
            pstmt.setString(6, LocalTime.now().toString());
            pstmt.executeUpdate();
            pstmt.close();
        }
        newOne.close();
    }
}
