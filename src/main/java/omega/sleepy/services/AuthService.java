package omega.sleepy.services;

import omega.sleepy.dao.UserDao;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
	
	public static boolean checkValidity(String username, String plainTextPassword){
		String passwordHash = UserDao.getPasswordHashFromUsername(username);
		return BCrypt.checkpw(plainTextPassword, passwordHash);
	}

	public static boolean changePassword(String username, String newPasswordPT, String oldPasswordPT){
		//check if new password is not old password
		//check if old password is valid
		//change password for the user
		return true;
	}

	public static boolean deleteProfile(String username, String password){
		// check if the user password is right, if so delete the profile
		return true;
	}

	public static String hashPassword(String plainTextPassword){
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}
}
