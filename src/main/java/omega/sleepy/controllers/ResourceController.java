package omega.sleepy.controllers;

import com.google.gson.Gson;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.routes.ApiRoutes;
import omega.sleepy.services.MiscService;
import omega.sleepy.services.ProfileService;
import omega.sleepy.util.MediaType;
import omega.sleepy.util.ProfileIcons;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.*;

import static omega.sleepy.controllers.ControllerUtil.missingResource;

public class ResourceController {

    private static final Gson gson = new Gson();

    public static String getStyleSheet(Request request, Response response) {
        String styleSheetName = request.params("styleSheet");
        response.type(MediaType.CSS.getValue());
        try (var inputStream = ApiRoutes.class.getResourceAsStream("/public/css/" + styleSheetName)) {
            if (inputStream == null) {
                return missingResource(response);
            }
            return IOUtils.toString(inputStream);
        } catch (Exception e) {
            response.status(500);
            return "/* Server Error loading CSS */";
        }
    }

    public static String getJavaScriptFile(Request request, Response response) {
        String styleSheetName = request.params("js");
        response.type(MediaType.JS.getValue());
        try (var inputStream = ApiRoutes.class.getResourceAsStream("/public/js/" + styleSheetName)) {
            if (inputStream == null) {
                return missingResource(response);
            }
            return IOUtils.toString(inputStream);
        } catch (Exception e) {
            response.status(500);
            return "/* Server Error loading JS */";
        }
    }


    public static Object getFavicon(Request request, Response response) {
        response.type(MediaType.ICON.getValue());
        response.header("Cache-Control", "public, max-age=604800"); // 1 week

        try (var inputStream = ApiController.class.getResourceAsStream("/public/img/icons/favicon.ico")) {
            if (inputStream == null) {
                return missingResource(response);
            }

            byte[] bytes = IOUtils.toByteArray(inputStream);
            response.raw().getOutputStream().write(bytes);
            response.raw().getOutputStream().flush();
        } catch (IOException e) {
            response.status(500);
            return "";
        }

        return "";
    }

    public static Object getImage(Request request, Response response) {
        response.type(MediaType.ICON.getValue());
        response.header("Cache-Control", "public, max-age=604800"); // 1 week

        String image = request.params("image");
        return MiscService.getImage(image, request, response);
    }

    public static Object getIcon(Request request, Response response){
        response.type(MediaType.SVG.getValue());
        response.header("Cache-Control", "public, max-age=604800"); // 1 week
        String icon = request.params("icon");
        if (icon == null) {
            return missingResource(response);
        }
        var iconBytes = ProfileService.getProfileIcon(icon);
        if (iconBytes == null) {
            return missingResource(response);
        }
        try {
            response.raw().getOutputStream().write(iconBytes);
            response.raw().getOutputStream().flush();
            return "";
        } catch (IOException e) {
            return missingResource(response);
        }
    }

    public static String getCategories(Request request, Response response) {
        return gson.toJson(BlogDao.getCategories());
    }

    public static Object getIcons(Request request, Response response) {
        List<ProfileIcons> icons = new ArrayList<>(Arrays.asList(ProfileIcons.values()));

        return gson.toJson(icons);

    }


    public static final Map<String, String> CSS_VARS = new LinkedHashMap<>();

    static {
        CSS_VARS.put("primary-color", "#5ad53d");
        CSS_VARS.put("primary-hover", "#3abd1d");
        CSS_VARS.put("primary-active", "#5ad53d");
        CSS_VARS.put("tetra-1", "#8496E8");
        CSS_VARS.put("tetra-1-dark", "#8486b8");
        CSS_VARS.put("tetra-2", "#E88496");
        CSS_VARS.put("tetra-2-dark", "#E07486");
        CSS_VARS.put("bg-body", "#f5f5f5");
        CSS_VARS.put("bg-card", "#ffffff");
        CSS_VARS.put("text-main", "#333333");
        CSS_VARS.put("text-muted", "#666666");
        CSS_VARS.put("text-light", "#888888");
        CSS_VARS.put("text-dark", "#222222");
        CSS_VARS.put("tag-border", "#dddddd65");
        CSS_VARS.put("border-color", "#dddddd");
        CSS_VARS.put("tag-bg", "#e9ecef");
        CSS_VARS.put("code-bg", "#111111");
        CSS_VARS.put("code-text", "#eeeeee");
    }

    public static Object getCssVars(Request request, Response response) {
        return gson.toJson(CSS_VARS);
    }
}
