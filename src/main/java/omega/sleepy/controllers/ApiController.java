package omega.sleepy.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.dao.UserDao;
import omega.sleepy.data.Blog;
import omega.sleepy.dto.UserRequestDTO;
import omega.sleepy.exceptions.InvalidCredentials;
import omega.sleepy.routes.ApiRoutes;
import omega.sleepy.services.AuthService;
import omega.sleepy.services.BlogService;
import omega.sleepy.services.MiscService;
import omega.sleepy.services.ProfileService;
import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import omega.sleepy.util.ProfileIcons;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static omega.sleepy.routes.PublicRoutes.templateEngine;
import static omega.sleepy.services.AuthService.validateToken;

public class ApiController {

    private static final Gson gson = new Gson();

    public static String getStyleSheet(Request request, Response response) {
        String styleSheetName = request.params("styleSheet");
        response.type("text/css");
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


    public static String createBlog(Request request, Response response) {
        JsonObject body = gson.fromJson(request.body(), JsonObject.class);

        String token = request.cookie(AuthController.AUTH_COOKIE);

        if (token == null) {
            return missingResource(response);
        }

        try{
            validateToken(token);
        } catch (InvalidCredentials e) {
            return missingResource(response);
        }

        String author = UserDao.usernameFromToken(token);

        String title = body.get("title").getAsString();
        String category = body.get("category").getAsString();
        String excerpt = body.get("excerpt").getAsString();
        String content = body.get("content").getAsString();

        boolean success = BlogService.saveBlog(title, category, excerpt, content, author);

        return gson.toJson(success ? "{\"status\":\"ok\"}" : "{\"status\":\"not ok\"}");
    }

    public static String getBlogPageById(Request request, Response response) {
        response.type(MediaType.HTML.getValue());
        int id = 0;

        try {
            id = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException e) {
            return missingResource(response);
        }

        Blog blog = BlogService.getBlogById(id);

        if(blog == null) {
            return missingResource(response);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("id", request.params(":id"));
        model.put("blog", blog);

        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process("blog_page", context);
    }


    public static String getBlogContentsById(Request request, Response response) {
        response.type(MediaType.TXT.getValue());
        var id = Integer.parseInt(request.params(":id"));
        String body = BlogService.getBlogBodyById(id);

        if(body == null) {
            return missingResource(response);
        }

        return body;
    }

    public static Object getFilteredView(Request request, Response response) {
        response.type(MediaType.JSON.getValue());
        String category = request.queryParams("category");
        String name = request.queryParams("name");
        String order = request.queryParams("order");
        String pageString = request.queryParams("page");

        int page = pageString == null ? 0 : Integer.parseInt(pageString);

        List<Blog> blogs = BlogService.getBlogsByFilter(name, category, order, page);

        Log.exec("Queried for " + category + " '" + name + "'");

        return gson.toJson(blogs);

    }

    public static String missingResource(Response response) {
        Log.error("something happened HERE \n" + response.raw());
        response.status(404);
        response.type(MediaType.JSON.getValue());
        response.redirect("/404");
        return "{\"status\":\"error\"}";
    }

    public static String forbitten(Response response) {
        Log.error("something happened HERE \n" + response.raw());
        response.status(401);
        response.type(MediaType.JSON.getValue());
        return "{\"status\":\"error\"}";
    }

    public static String getUserInformation(Request request, Response response) {
        String token = request.cookie(AuthController.AUTH_COOKIE);

        if (token == null) {
            return missingResource(response);
        }

        String username = AuthService.getUsernameByToken(token);

        if (username == null) {
            return missingResource(response);
        } else if (!AuthService.userExists(username)) {
            return AuthController.logout(request, response);
        }

        System.out.println(username);

        response.type(MediaType.JSON.getValue());
        System.out.println(username);
        String iconName = UserDao.getPfp(username);

        ProfileIcons icon = ProfileIcons.valueOf(iconName.toUpperCase());

        return gson.toJson(new UserRequestDTO(username, icon.name().toLowerCase().replace("_","-")));
    }

    public static Object deleteBlog(Request request, Response response) {
        String token = request.cookie(AuthController.AUTH_COOKIE);

        if (token == null) {
            return forbitten(response);
        }

        String username = AuthService.getUsernameByToken(token);

        if (username == null) {
            return forbitten(response);
        }

        String blogId = request.params("id");

        int id;
        try {
            id = Integer.parseInt(blogId);

            if(!BlogService.canEdit(id, username)){
                return forbitten(response);
            }

            if(BlogService.deleteBlogById(id)) {
                response.status(200);
                response.type(MediaType.JSON.getValue());
                return "{\"status\":\"ok\"}";

            } else {
                return forbitten(response);
            }

        } catch (NumberFormatException e) {
            return forbitten(response);
        }

    }

    public static Object editBlog(Request request, Response response) {
        return "";
    }

    public static Object checkCanEdit(Request request, Response response) {
        String token = request.cookie(AuthController.AUTH_COOKIE);
        if (token == null) {
            return missingResource(response);
        }

        String username = AuthService.getUsernameByToken(token);

        if (username == null) {
            return forbitten(response);
        }
        String blogId = request.params(":id");
        int id;

        try {
            id = Integer.parseInt(blogId);

            if(BlogService.canEdit(id, username)){
                response.status(200);
                response.type(MediaType.JSON.getValue());
                return "{\"status\":\"ok\"}";
            } else {
                return forbitten(response);
            }

        } catch (NumberFormatException e) {
            return missingResource(response);
        }

    }
}
