package omega.sleepy.dao;

import omega.sleepy.exceptions.UserAlreadyExists;
import omega.sleepy.exceptions.UserDoesNotExist;
import omega.sleepy.util.Database;
import omega.sleepy.util.Log;
import omega.sleepy.util.PermittingLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;

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

    public static void createUser(String username, String passwordHash) {
        String sql = "INSERT into users values(?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);
            preparedStatement.setString(3, PermittingLevel.USER.toString());
            preparedStatement.setString(4, LocalDateTime.now().toString());
            preparedStatement.setString(5, LocalDateTime.now().toString());

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new UserAlreadyExists("User by the same username %s already exists".formatted(username));
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
                return resultSet.getString("token").equals(token);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    }
