package omega.sleepy.services;

import omega.sleepy.util.ProfileIcons;
import omega.sleepy.util.ProfileIcons.*;
import spark.utils.IOUtils;

import java.io.IOException;

public class ProfileService {
    public static byte[] getProfileIcon(String name){
        if (name == null) {
            return null;
        }

        ProfileIcons icon;

        try{
            icon = ProfileIcons.valueOf(name.toUpperCase());
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
}
