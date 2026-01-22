package omega.sleepy.dao;

import omega.sleepy.services.AuthService;
import omega.sleepy.util.Database;
import omega.sleepy.util.Log;
import omega.sleepy.util.PermittingLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserDao {
    public static String getPasswordHashFromUsername(String username){
        String sql = "SELECT password_hash from users where username = ?";

        try (Connection connection = Database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, username);

            return preparedStatement.executeQuery().getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean createUser(String username, String passwordHash){
        String sql = "INSERT into users values(?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwordHash);
            preparedStatement.setString(3, PermittingLevel.USER.toString());
            preparedStatement.setString(4, LocalDateTime.now().toString());
            preparedStatement.setString(5, LocalDateTime.now().toString());

            preparedStatement.execute();
            Log.info("Created new user acc by - " + username); //TODO remove this and put it in /service
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean deleteUser(String username){
        String sql = "DELETE from users where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, username);
            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    public static boolean changePassword(String username, String newPasswordHash){
        String sql = "UPDATE users set password_hash = ? where username = ?";
        try (Connection connection = Database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql))   {

            preparedStatement.setString(1, newPasswordHash);
            preparedStatement.setString(2, username);

            preparedStatement.execute();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean changeUserPrivalages(String username, PermittingLevel permittingLevel){
        String sql = "UPDATE users set permittion_level = ? where username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql))   {

            preparedStatement.setString(1, permittingLevel.toString());
            preparedStatement.setString(2, username);

            preparedStatement.execute();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String username = "Martin";
        String plainTextPass = "I-love-birds";
        String hash = AuthService.hashPassword(plainTextPass);
        if (createUser(username, hash)) {
            Log.info("Successful profile creation");
        } else {
            Log.error("User already exists with the same username");
        }
        if(AuthService.checkValidity(username, plainTextPass)){
            Log.info("Password is correct");
        } else {
            Log.error("Password is incorrect");
        }
        changeUserPrivalages("OmegaSleepy", PermittingLevel.ADMIN);

        plainTextPass = "I-love-pigeons";
        hash = AuthService.hashPassword(plainTextPass);
        if(changePassword(username, hash)) {
            Log.info("Successful password change for user %s".formatted(username));
        } else {
            Log.error("Unsuccessful password change for user %s".formatted(username));
        }

    }

}
