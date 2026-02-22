package omega.sleepy.services;

import com.google.gson.JsonElement;
import omega.sleepy.dao.StyleDao;
import omega.sleepy.dao.UserDao;
import omega.sleepy.data.User;
import omega.sleepy.data.UserWithExtras;
import omega.sleepy.util.Log;
import omega.sleepy.util.enums.PermittingLevel;
import omega.sleepy.util.enums.ProfileIcons;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProfileService {
    public static byte[] getProfileIcon(String name){
        if (name == null) {
            return null;
        }

        ProfileIcons icon;

        try{
            String clean = ProfileIcons.getProfileStyle(name);
            Log.warn(clean);
            icon = ProfileIcons.valueOf(clean);
            return getProfileIcon(icon);

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static byte[] getProfileIcon(ProfileIcons icon){
        try (var inputStream = ProfileService.class.getResourceAsStream("/public/img/profileIcons/"+icon.location())){
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

    public static UserWithExtras getFullProfile(String username){
        List<String> userInfo = UserDao.getAllUserInfo(username);
        if (userInfo.isEmpty()) {
            return null;
        }

        return new UserWithExtras(username,
                userInfo.get(1),
                PermittingLevel.valueOf(userInfo.get(2).toUpperCase()),
                userInfo.get(3),
                userInfo.get(4),
                ProfileIcons.valueOf(userInfo.get(5).toUpperCase()),
                Integer.parseInt(userInfo.get(7)),
                userInfo.get(8));
    }

    public static String getStyleFromUser(String username){
        var value = StyleDao.getStyleFromUser(username);
        value = value.replace("\n", " ");
        value = value.replace("\"", " ");
        value = ":root { " + value + " }";
        return value;
    }

    public static void setStyleForUser(Map<String, JsonElement> map, String username) {
        StringBuilder value = new StringBuilder();
        map.forEach((k,v) -> value.append("--").append(k).append(": ").append(v).append(";\n"));
        StyleDao.setStyleForUser(value.toString(), username);
    }
}
