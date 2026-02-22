package omega.sleepy.dao;

import omega.sleepy.util.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StyleDao {
    public static String getStyleFromUser(String username) {
        String sql = "SELECT style_profile from user_extras WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()){
                return rs.getString("style_profile");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setStyleForUser(String map, String username) {
        String sql = "UPDATE user_extras SET style_profile = ? WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, map);
            preparedStatement.setString(2, username);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
