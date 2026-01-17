package omega.sleepy.dao;

import omega.sleepy.data.Blog;
import omega.sleepy.util.BlogFilter;
import omega.sleepy.util.Direction;
import omega.sleepy.util.FileUtil;
import omega.sleepy.util.Log;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static omega.sleepy.util.Database.getConnection;

public class BlogDao {

    private static List<String> categories;
    private static String any;

    public static List<String> getCategories() {
        return categories;
    }

    public static String getDefaultCategory(){
        return any;
    }

    public static void init() {

        any = "Всякакви";

        categories = List.of("Математика", "Наука", "Биология", "Химия", "Физика", "Английски език", "История",
                "География", "Изкуство", "Музика", "Компютърни науки", "Икономика", "Философия",
                "Литература", "Няма", any);


    }

    public static void addBlog(Blog blog) {
        Log.error("Saving " + blog.toString());
        String addBlogSQL = FileUtil.readFile("/sql/blog/addBlog.sql");
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addBlogSQL)
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
            return null;
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

    private static String getResultSetString(ResultSet rs, String column) {
        String string;
        try{
            string = rs.getString(column);
        } catch (SQLException e) {
            string = "?";
        }
        return string;
    }


    public static List<Blog> getBlogsByFilter(BlogFilter blogFilter) {
        List<Blog> blogList = new ArrayList<>();

        StringBuilder sql = getSQLByBlogFilter(blogFilter);

        Log.info(sql.toString());

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    blogList.add(getBlog(rs));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return blogList;
    }

    private static @NotNull StringBuilder getSQLByBlogFilter(BlogFilter blogFilter) {
        String name = blogFilter.getTitle();
        Direction orderDirection = blogFilter.getDirection();
        String category = blogFilter.getCategory();
        int page = blogFilter.getPage();

        String order = orderDirection.toString().toLowerCase();

        boolean isAny = category.equalsIgnoreCase(any);

        StringBuilder sql = new StringBuilder("SELECT * FROM blogs where ");

        if(!isAny) sql.append("tag = '%s' AND ".formatted(category));
        sql.append("title like '%%%s%%' ".formatted(name));
        sql.append("ORDER BY created_at %s ".formatted(order));
        sql.append("LIMIT 15 OFFSET %d;".formatted(page*15));
        return sql;
    }
}
