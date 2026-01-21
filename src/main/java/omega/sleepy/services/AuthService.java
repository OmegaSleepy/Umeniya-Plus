package omega.sleepy.services;

import omega.sleepy.dao.UserDao;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
	
	public static boolean checkValidity(String password, String username){
		String passwordHash = UserDao.getPasswordHashFromUsername(username);
		return BCrypt.checkpw(password, passwordHash);
		
	}

	public static boolean changePassword(String password, String username){

	}

	public static boolean deleteProfile(String username){

	}
	
}
