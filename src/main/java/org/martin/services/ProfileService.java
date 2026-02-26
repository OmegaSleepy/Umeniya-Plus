package org.martin.services;

import com.google.gson.JsonElement;
import org.martin.dao.UserDao;
import org.martin.data.User;
import org.martin.data.UserWithExtras;
import org.martin.util.Log;
import org.martin.util.enums.PermittingLevel;
import org.martin.util.enums.ProfileIcons;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This service is used for profile customization.
 * @see org.martin.controllers.ApiController
 * @see org.martin.routes.ApiRoutes
 * @see ProfileIcons
 * **/
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
        var value = UserDao.getStyleFromUser(username);
        value = value.replace("\n", " ");
        value = value.replace("\"", " ");
        value = ":root { " + value + " }";
        return value;
    }

    public static void setStyleForUser(Map<String, JsonElement> map, String username) {
        StringBuilder value = new StringBuilder();
        map.forEach((k,v) -> value.append("--").append(k).append(": ").append(v).append(";\n"));
        UserDao.setStyleForUser(value.toString(), username);
        Log.exec(value.toString());
    }

    public static boolean checkAndDeductFunds(int funds, String user) {
        int currentFunds = UserDao.getFlamesFromUsername(user);
        if (currentFunds <= funds) {
            return false;
        }
        UserDao.setFlamesToUsername(currentFunds-funds, user);
        return true;
    }

    public static void addFunds(int funds, String user) {
        int currentFunds = UserDao.getFlamesFromUsername(user);
        UserDao.setFlamesToUsername(currentFunds+funds, user);
    }

    public static int checkFunds(String user) {
        return UserDao.getFlamesFromUsername(user);
    }
}
