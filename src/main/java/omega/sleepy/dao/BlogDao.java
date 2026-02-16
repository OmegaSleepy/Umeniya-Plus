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
import java.util.Map;
import java.util.concurrent.locks.Condition;

import static omega.sleepy.util.Database.getConnection;

public class BlogDao {

    private static final String DEFAULT_COLOR = "#F2F2F2";

    private static final String any = "Всякакви";

    private static final Map<String, String> CATEGORIES = Map.ofEntries(
            Map.entry("Математика", "#DCEBFF"),
            Map.entry("Наука", "#D9F4F1"),
            Map.entry("Биология", "#E3F6E8"),
            Map.entry("Химия", "#EFE3FF"),
            Map.entry("Физика", "#E1F0FF"),
            Map.entry("Английски език", "#FFE3E3"),
            Map.entry("История", "#F5EAD6"),
            Map.entry("География", "#EEF3D9"),
            Map.entry("Изкуство", "#FFEBD6"),
            Map.entry("Музика", "#F2E6FF"),
            Map.entry("Компютърни науки", "#E3EAF5"),
            Map.entry("Икономика", "#E6F7F1"),
            Map.entry("Философия", "#ECE9F4"),
            Map.entry("Литература", "#FFF4E1"),
            Map.entry("Няма", DEFAULT_COLOR),
            Map.entry(any, DEFAULT_COLOR)
    );

    public static Map<String, String> getCategories() {
        return CATEGORIES;
    }

    public static String getDefaultCategory() {
        return any;
    }

    public static void init() {
    }

    public static void addBlog(Blog blog) {
        Log.error("Saving " + blog.toString());
        String sql = "INSERT into blogs(title, tag, excerpt, content, creator_username, created_at) values (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
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
        String sql = "SELECT * FROM blogs WHERE id = ? limit 1";
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

    public static List<Blog> getBlogView() {
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
             ResultSet resultSet = statement.executeQuery(sql)) {

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
        try {
            string = rs.getString(column);
        } catch (SQLException e) {
            string = "?";
        }
        return string;
    }

    public static List<Blog> getBlogByAuthor(String user) {
        List<Blog> blogList = new ArrayList<>();

        String sql = "SELECT * FROM blogs WHERE creator_username = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user);
            try (ResultSet rs = preparedStatement.executeQuery()) {
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

    public static List<Blog> getBlogsByFilter(BlogFilter blogFilter) {
        List<Blog> blogList = new ArrayList<>();

        Log.info("Searching " + blogFilter.toString());

        int isAny = blogFilter.getCategory().equalsIgnoreCase(any) ? 1 : 0;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getSQLByBlogFilter(blogFilter))) {

            int i = 1;

            if (isAny == 0) {
                pstmt.setString(i++, blogFilter.getCategory());
            }

            pstmt.setString(i++, "%"+ blogFilter.getTitle() + "%");
            pstmt.setInt(i, blogFilter.getPage() * 16);


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

    private static @NotNull String getSQLByBlogFilter(BlogFilter blogFilter) throws SQLException {
        Direction orderDirection = blogFilter.getDirection();
        String category = blogFilter.getCategory();

        String order = orderDirection.toString().toLowerCase();

        boolean isAny = category.equalsIgnoreCase(any);

        StringBuilder sql = new StringBuilder("SELECT * FROM blogs where ");

        if (!isAny) sql.append("tag = ? AND ");// (category));
        sql.append("title like ? ");//(name));
        sql.append("ORDER BY created_at %s ".formatted(order));
        sql.append("LIMIT 16 OFFSET ? ;");//(page * 15));

        Log.info("SQL: " + sql);

        return sql.toString();
    }


    public static boolean deleteBlogById(int id) {
        String sql = "DELETE FROM blogs WHERE id = ? limit 1";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            Log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
