package omega.sleepy.dao;

import omega.sleepy.data.Blog;
import omega.sleepy.exceptions.MissingResource;
import omega.sleepy.util.Database;
import omega.sleepy.util.FileUtil;
import omega.sleepy.util.Log;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
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
        Log.error("Saving " + blog.toString());
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

            preparedStatement.execute();

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
                return getBlog(rs);
            }
        } catch (SQLException e) {
            throw new MissingResource(e.getMessage());
        }
    }

    public static List<Blog> getBlogView(){
        List<Blog> blogList = new ArrayList<>();
        String sql = "SELECT * FROM blogs LIMIT 10";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                blogList.add(getBlog(rs));
            }
            Log.info("Found " + blogList.size() + " blogs");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return blogList;
    }

    public static List<Blog> getBlogWithoutContents() {
        List<Blog> blogList = new ArrayList<>();
        String sql = "SELECT id, title, tag, excerpt, creator_username, created_at FROM blogs LIMIT 10";

        try (Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            while (resultSet.next()) {
                blogList.add(getBlog(resultSet));
            }

            return blogList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull Blog getBlog(ResultSet rs) throws SQLException {
        return new Blog(
                rs.getInt("id"),
                getResultSetString(rs, "title"),
                getResultSetString(rs, "tag"),
                getResultSetString(rs, "excerpt"),
                getResultSetString(rs, "content"),
                getResultSetString(rs, "creator_username"),
                getResultSetString(rs, "created_at")
        );
    }

    private static String getResultSetString(ResultSet rs, String coloumn) throws SQLException {
        String string;
        try{
            string = rs.getString(coloumn);
        } catch (SQLException e) {
            string = "?";
        }
        return string;
    }


}
