package omega.sleepy.dao;

import omega.sleepy.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StyleDao {
    //TODO get style from username
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
