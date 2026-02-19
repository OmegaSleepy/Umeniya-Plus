package omega.sleepy.dao;

import omega.sleepy.exceptions.UserAlreadyExists;
import omega.sleepy.exceptions.UserDoesNotExist;
import omega.sleepy.util.Database;
import omega.sleepy.util.Log;
import omega.sleepy.util.PermittingLevel;
import omega.sleepy.util.ProfileIcons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static omega.sleepy.util.Log.error;

public class UserDao {
    public static String getPasswordHashFromUsername(String username) {
        String sql = "SELECT password_hash from users where username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            return preparedStatement.executeQuery().getString(1);
        } catch (SQLException e) {
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }
    }

    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void createUser(String username, String passwordHash) {
        String sql = "INSERT into users values(?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);
            preparedStatement.setString(3, PermittingLevel.USER.toString());
            preparedStatement.setString(4, LocalDateTime.now().toString());
            preparedStatement.setString(5, LocalDateTime.now().toString());
            preparedStatement.setString(6, ProfileIcons.getRandom().name());

            preparedStatement.execute();
        } catch (SQLException e) {
            Log.error(e.getMessage());
            throw new UserAlreadyExists("Потребителското име е заето");
        }

        sql = "INSERT into user_extras values(?, ?, ?)";

        Log.info(sql);

        try (Connection connection = Database.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, 250);
            preparedStatement.setString(3, "");
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteUser(String username) {
        String sql = "DELETE from users where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }

    }

    public static String getPfp(String username){
        String sql = "SELECT profile_icon from users where username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            return preparedStatement.executeQuery().getString(1);
        } catch (SQLException e) {
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }
    }

    public static void changePfp(String username, String icon){
        String sql = "UPDATE users set profile_icon = ? where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, icon);
            preparedStatement.setString(2, username);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }
    }

    public static void changePassword(String username, String newPasswordHash) {
        String sql = "UPDATE users set password_hash = ? where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, newPasswordHash);
            preparedStatement.setString(2, username);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }
    }

    public static void changeUserPrivalages(String username, PermittingLevel permittingLevel) {
        String sql = "UPDATE users set permittion_level = ? where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, permittingLevel.toString());
            preparedStatement.setString(2, username);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }
    }

    public static void addSession(String token, String username, long expiration) {
        String sql = "INSERT into sessions (token, username, expires_at) values (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, username);
            preparedStatement.setLong(3, expiration);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeSession(String token) {
        String sql = "DELETE from sessions where token = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, token);
            preparedStatement.execute();
            Log.info("Removed session with UUID " + token);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean containsToken(String token) {
        String sql = "SELECT * from sessions where token = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, token);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                String dBToken = resultSet.getString("token");
                if (dBToken != null) {
                    return dBToken.equals(token);
                }
                return false;
            }

        } catch (SQLException e) {
            error(e.getMessage());
	        return false;
        }
    }

    public static void deleteOldTokens() {
        String sql = "DELETE from sessions where expires_at < ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, Instant.now().getEpochSecond());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String usernameFromToken(String token) {
        String sql = "SELECT username from sessions where token = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, token);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getUserInfo(String username) {
        String sql = "SELECT * from users where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<String> list = new ArrayList<>();
                list.add(username);
                list.add(resultSet.getString("password_hash"));
                list.add(resultSet.getString("permittion_level"));
                list.add(resultSet.getString("registrated_at"));
                list.add(resultSet.getString("last_login"));
                list.add(resultSet.getString("profile_icon"));
                return list;
            }
        } catch (SQLException e) {
            Log.error(e.getMessage());
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }
    }

    public static List<String> getAdditionalUserInfo(String username) {
        String sql = "SELECT * from user_extras where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<String> list = new ArrayList<>();
                list.add(username);
                list.add(String.valueOf(resultSet.getInt("flames")));
                list.add(resultSet.getString("style_profile"));
                return list;
            }
        } catch (SQLException e) {
            Log.error(e.getMessage());
            throw new UserDoesNotExist("User by the username %s, does not exist".formatted(username));
        }
    }

    public static List<String> getAllUserInfo(String username) {
        var list = getUserInfo(username);
        list.addAll(getAdditionalUserInfo(username));
        return list;
    }
}
