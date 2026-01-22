package omega.sleepy.services;

import omega.sleepy.dao.UserDao;
import org.mindrot.jbcrypt.BCrypt;
import static omega.sleepy.validation.UserValidator.*;

public class AuthService {
	
	public static boolean isPasswordValid(String username, String plainTextPassword){
		String passwordHash = UserDao.getPasswordHashFromUsername(username);
		return BCrypt.checkpw(plainTextPassword, passwordHash);
	}

	public static boolean changePassword(String username, String newPasswordPT, String oldPasswordPT){
		if(username.equals(newPasswordPT)) return false;
		if(!isPasswordValid(username, oldPasswordPT)) return false;
		if(!isPasswordFormatValid(newPasswordPT)) return false;

		return UserDao.changePassword(username, hashPassword(newPasswordPT));
	}

	public static boolean deleteProfile(String username, String plainTextPassword){
		if(!isPasswordValid(username, plainTextPassword)) return false;
		return UserDao.deleteUser(username);
	}

	public static boolean createUser(String username, String plainTextPassword){
		if(!isPasswordFormatValid(plainTextPassword)) return false;
		return UserDao.createUser(username, hashPassword(plainTextPassword));
	}

	public static String hashPassword(String plainTextPassword){
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}
}
