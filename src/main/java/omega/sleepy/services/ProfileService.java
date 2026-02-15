package omega.sleepy.services;

import omega.sleepy.dao.UserDao;
import omega.sleepy.data.User;
import omega.sleepy.util.Log;
import omega.sleepy.util.PermittingLevel;
import omega.sleepy.util.ProfileIcons;
import omega.sleepy.util.ProfileIcons.*;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ProfileService {
    public static byte[] getProfileIcon(String name){
        if (name == null) {
            return null;
        }

        ProfileIcons icon;

        try{
            String clean = name.toUpperCase().replace("-","_");
            icon = ProfileIcons.valueOf(clean);
            return getProfileIcon(icon);

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static byte[] getProfileIcon(ProfileIcons icon){
        try (var inputStream = ProfileService.class.getResourceAsStream("/public/img/profileIcons/"+icon.toString())){
            if (inputStream == null) {
                return null;
            }
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getProfile(String username){
        List<String> userInfo = UserDao.getUserInfo(username);

        if (userInfo.isEmpty()) {
            return null;
        }

        return new User(username,
                        userInfo.get(1),
                        PermittingLevel.valueOf(userInfo.get(2).toUpperCase()),
                        userInfo.get(3),
                        userInfo.get(4),
                        ProfileIcons.valueOf(userInfo.get(5).toUpperCase()));
    }
}
