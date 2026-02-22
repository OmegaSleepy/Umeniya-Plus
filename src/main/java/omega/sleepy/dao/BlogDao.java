package omega.sleepy.dao;

import omega.sleepy.data.Blog;
import omega.sleepy.util.BlogFilter;
import omega.sleepy.util.enums.Direction;
import omega.sleepy.util.Log;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

import static omega.sleepy.util.db.Database.getConnection;

public class BlogDao {

    private static final String DEFAULT_COLOR = "#F2F2F2";

    private static final String any = "Всякакви";

    private static final Map<String, String> CATEGORIES;

    static {
        Map<String, String> map = new LinkedHashMap<>();

        map.put("Математика", "#DCEBFF");
        map.put("Наука", "#D9F4F1");
        map.put("Биология", "#E3F6E8");
        map.put("Химия", "#EFE3FF");
        map.put("Физика", "#E1F0FF");
        map.put("Английски език", "#FFE3E3");
        map.put("История", "#F5EAD6");
        map.put("География", "#EEF3D9");
        map.put("Изкуство", "#FFEBD6");
        map.put("Музика", "#F2E6FF");
        map.put("Компютърни науки", "#E3EAF5");
        map.put("Икономика", "#E6F7F1");
        map.put("Философия", "#ECE9F4");
        map.put("Литература", "#FFF4E1");
        map.put("Няма", DEFAULT_COLOR);
        map.put(any, DEFAULT_COLOR);

        CATEGORIES = Collections.unmodifiableMap(map);
    }


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
                getResultSetString(rs, "created_at"),
                rs.getInt("tax"),
                rs.getInt("views")
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

        String sql = "SELECT id, title, tag, excerpt, creator_username, created_at, tax, views FROM blogs WHERE creator_username = ?";

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

    public static boolean hasRecordOf(String user, int page) {
        String sql = "SELECT user, id FROM readBlogs where user = ? and id = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, user);
            preparedStatement.setInt(2, page);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            Log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void addRecordOfReadBlog(String user, int page) {
        String sql = "INSERT INTO readBlogs values (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, user);
            preparedStatement.setInt(2, page);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void addOneView(int id) {
        String sql = "UPDATE blogs set views = views + 1 where id = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static boolean replaceBlog(int id, Blog newBlog) {
        String sql = "UPDATE blogs SET title = ?, tag = ?, excerpt = ?, content = ? where id = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, newBlog.title());
            preparedStatement.setString(2, newBlog.tag());
            preparedStatement.setString(3, newBlog.excerpt());
            preparedStatement.setString(4, newBlog.content());
            preparedStatement.setInt(5, id);

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            Log.error(e.getMessage());
            return false;
        }
    }

    public static int totalViewsByAuthor(String author) {
        String sql = "SELECT sum(views) FROM blogs WHERE creator_username = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, author);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            Log.error(e.getMessage());
            return -3;
        }
    }
}
