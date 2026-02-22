package omega.sleepy.services;

import omega.sleepy.dao.UserDao;
import omega.sleepy.exceptions.InvalidCredentials;
import omega.sleepy.exceptions.InvalidPassword;
import omega.sleepy.exceptions.MalformedPassword;
import omega.sleepy.util.Log;
import org.mindrot.jbcrypt.BCrypt;

import java.security.InvalidParameterException;

import static omega.sleepy.validation.UserValidator.*;

public class AuthService {
	
	public static boolean isPasswordInvalid(String username, String plainTextPassword){
		String passwordHash = UserDao.getPasswordHashFromUsername(username);
		if (passwordHash == null) {
			return false;
		}
		return !BCrypt.checkpw(plainTextPassword, passwordHash);
	}

	public static void changePassword(String username, String newPasswordPT, String oldPasswordPT) throws InvalidPassword, MalformedPassword, InvalidParameterException{
		if(username.equals(newPasswordPT)) throw new InvalidParameterException("New and Old password match");
		if(isPasswordInvalid(username, oldPasswordPT)) throw new InvalidPassword("Old password is not correct");
		if(isPasswordFormatInvalid(newPasswordPT)) throw new MalformedPassword("New password does not have correct format");

		UserDao.changePassword(username, hashPassword(newPasswordPT));
	}

	public static void deleteProfile(String username, String plainTextPassword) throws InvalidPassword{
		if(isPasswordInvalid(username, plainTextPassword)) throw new InvalidPassword("Password is not correct");
		UserDao.deleteUser(username);
	}

	public static void login(String username, String plainTextPassword) throws InvalidPassword{
		if(isPasswordInvalid(username, plainTextPassword)) throw new InvalidPassword("Password is not correct");
	}

	public static String getUsernameByToken(String token){
		return UserDao.usernameFromToken(token);
	}

	public static void createUser(String username, String plainTextPassword) throws RuntimeException{
		if(isPasswordFormatInvalid(plainTextPassword)) throw new MalformedPassword("Паролата не е достатъчно силна");
		UserDao.createUser(username, hashPassword(plainTextPassword));
		Log.exec("Created a new userprofile by %s".formatted(username));
	}

	public static boolean userExists(String username){
		return UserDao.userExists(username);
	}

	public static String hashPassword(String plainTextPassword){
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}

	public static void validateToken(String token) throws InvalidCredentials {
		if (token == null) {
			Log.warn("No token!");
			Log.warn("No cookie!");
			throw new InvalidCredentials("No token, No cookie");
		}
		Log.info(token);
		if(UserDao.containsToken(token)) {
			Log.info("Valid session");
		} else {
			Log.warn("Invalid session");
			throw new InvalidCredentials("Token either expired or is not valid");
		}
	}

	public static boolean changeProfilePicture(String username, String icon) {
		return UserDao.changePfp(username, icon.toUpperCase());
	}
}
