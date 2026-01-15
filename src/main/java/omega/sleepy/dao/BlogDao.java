package omega.sleepy.dao;

import omega.sleepy.data.Blog;
import omega.sleepy.exceptions.MissingResource;
import omega.sleepy.util.Database;
import omega.sleepy.util.FileUtil;
import omega.sleepy.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static omega.sleepy.util.Database.getConnection;

public class BlogDao {

    private static List<String> categories;

    public static List<String> getCategories() {
        return categories;
    }

    public static void init() {

        categories = List.of("Mathematics", "Science", "Biology", "Chemistry", "Physics", "English", "History",
                "Geography", "Art", "Music", "Computer Science", "Economics", "Philosophy",
                "Literature", "None", "Any");
    }

    public static void addBlog(Blog blog) {
        Log.error("Logging " + blog.toString());
        String addBlogSQL = FileUtil.readFile("/sql/blog/addBlog.sql");
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addBlogSQL);
        ) {
            preparedStatement.setString(1, blog.title());
            preparedStatement.setString(2, blog.tag());
            preparedStatement.setString(3, blog.excerpt());
            preparedStatement.setString(4, blog.content());
            preparedStatement.setString(5, blog.creator());
            preparedStatement.setString(6, blog.creationDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Blog getBlogById(int id) {
        String sql = "SELECT * FROM blogs WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return new Blog(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("tag"),
                        rs.getString("excerpt"),
                        rs.getString("content"),
                        rs.getString("creator_username"),
                        rs.getString("creation_date")
                );
            }
        } catch (SQLException e) {
            throw new MissingResource(e.getMessage());
        }
    }

}
