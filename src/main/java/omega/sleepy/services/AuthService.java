package omega.sleepy.services;

import omega.sleepy.dao.UserDao;
import omega.sleepy.exceptions.InvalidPassword;
import omega.sleepy.exceptions.MalformedPassword;
import omega.sleepy.util.Log;
import org.mindrot.jbcrypt.BCrypt;

import java.security.InvalidParameterException;

import static omega.sleepy.validation.UserValidator.*;

public class AuthService {
	
	private static boolean isPasswordInvalid(String username, String plainTextPassword){
		String passwordHash = UserDao.getPasswordHashFromUsername(username);
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

	public static void createUser(String username, String plainTextPassword) throws MalformedPassword{
		if(isPasswordFormatInvalid(plainTextPassword)) throw new MalformedPassword("Password format is not correct");
		UserDao.createUser(username, hashPassword(plainTextPassword));
		Log.exec("Created a new userprofile by %s".formatted(username));
	}

	public static String hashPassword(String plainTextPassword){
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}
}
