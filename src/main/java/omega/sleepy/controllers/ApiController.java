package omega.sleepy.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import omega.sleepy.dao.UserDao;
import omega.sleepy.data.Blog;
import omega.sleepy.dto.UserRequestDTO;
import omega.sleepy.exceptions.InvalidCredentials;
import omega.sleepy.services.AuthService;
import omega.sleepy.services.BlogService;
import omega.sleepy.services.ProfileService;
import omega.sleepy.util.Log;
import omega.sleepy.util.enums.MediaType;
import omega.sleepy.util.enums.ProfileIcons;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static omega.sleepy.controllers.ControllerUtil.*;
import static omega.sleepy.routes.PublicRoutes.templateEngine;
import static omega.sleepy.services.AuthService.validateToken;

public class ApiController {

    private static final Gson gson = new Gson();

    public static String createBlog(Request request, Response response) {
        JsonObject body = gson.fromJson(request.body(), JsonObject.class);

        String token = request.cookie(AuthController.AUTH_COOKIE);

        if (token == null) {
            return forbitten(response);
        }

        try {
            validateToken(token);
        } catch (InvalidCredentials e) {
            return forbitten(response);
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
            return missingResourcePage(response);
        }

        Blog blog = BlogService.getBlogById(id);

        if (blog == null) {
            return missingResourcePage(response);
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

        if (body == null) {
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

        var user = ProfileService.getFullProfile(username);

        System.out.println(username);

        assert user != null;
        ProfileIcons icon = user.ProfileIcon();

        response.type(MediaType.JSON.getValue());
        return gson.toJson(new UserRequestDTO(username, icon.toString(), user.permittingLevel().toString(), user.flames()));
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

            if (!BlogService.canEdit(id, username)) {
                return forbitten(response);
            }

            if (BlogService.deleteBlogById(id)) {
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

            if (!BlogService.canEdit(id, username)) {
                return forbitten(response);
            }

            response.redirect("/edit/" + blogId);

            return "{\"status\":\"ok\"}";

        } catch (NumberFormatException e) {
            return missingResource(response);
        }
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

            if (BlogService.canEdit(id, username)) {
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

    public static Object getSpecificUserInformation(Request request, Response response) {

        String username = request.params("user");

        if (username == null) {
            return missingResource(response);
        } else if (!AuthService.userExists(username)) {
            return missingResource(response);
        }

        var user = ProfileService.getFullProfile(username);

        Log.info("Sending information about " + username);

        assert user != null;
        ProfileIcons icon = user.ProfileIcon();

        response.type(MediaType.JSON.getValue());
        return gson.toJson(new UserRequestDTO(username, icon.toString(), user.permittingLevel().toString(), user.flames()));
    }

    public static Object getByAuthor(Request request, Response response) {
        String author = request.params("author");
        if (author == null) return missingResource(response);

        var list = BlogService.getBlogsByAuthor(author);

        if (list == null) return missingResource(response);

        return gson.toJson(list);
    }

    public static Object rewardReader(Request request, Response response) {
        JsonObject json = gson.fromJson(request.body(), JsonObject.class);

        String user = json.get("user").getAsString();
        String author = json.get("author").getAsString();
        int page = json.get("page").getAsInt();

        var res = BlogService.award(user, author, page);

        if (res == null) {
            response.status(406);
            return "{\"status\":\"error\"}";
        }

        System.out.println(author + " " + user + " " + page);

        response.type(MediaType.JSON.getValue());
        return "{\"reward\":10}";
    }

    public static Object getBlogById(Request request, Response response) {
        String id = request.params("id");
        if (id == null) return missingResource(response);
        try {
            int index = Integer.parseInt(id);
            Blog blog = BlogService.getBlogById(index);
            return gson.toJson(blog);
        } catch (NumberFormatException e) {
            return missingResource(response);
        }
    }

    public static Object updateBlog(Request request, Response response) {
        String token = request.cookie(AuthController.AUTH_COOKIE);
        String blogId = request.params("id");

        String username = AuthService.getUsernameByToken(token);
        if (username == null) {
            Log.error("Invalid token");
            return forbitten(response);
        }

        try {
            int id = Integer.parseInt(blogId);
            if(!BlogService.canEdit(id, username)){
                Log.error("No edit perms");
                return forbitten(response);
            }
        } catch (NumberFormatException e) {
            return missingResource(response);
        }

        JsonObject json = gson.fromJson(request.body(), JsonObject.class);

        String title =  json.get("title").getAsString();
        String content = json.get("content").getAsString();
        String excerpt = json.get("excerpt").getAsString();
        String category =  json.get("category").getAsString();

        var serviceResponse = BlogService.updateBlog(title, category, excerpt, content, blogId);

        if(serviceResponse instanceof Exception e) {

            return gson.toJson(new RuntimeException(e));
            
        } else if(serviceResponse == null) {
            Log.error("Failed to update blog");
            return forbitten(response);
        }
        return gson.toJson(serviceResponse);
    }

    public static Object createCSSForUser(Request request, Response response) {
        String token = request.cookie(AuthController.AUTH_COOKIE);
        String username = AuthService.getUsernameByToken(token);

        if (username == null) {
            return forbitten(response);
        }

        if(!ProfileService.checkAndDeductFunds(200, username)) {
            return forbitten(response);
        }

        JsonObject json = gson.fromJson(request.body(), JsonObject.class);
        ProfileService.setStyleForUser(json.asMap(), username);
        response.type(MediaType.JSON.getValue());
        return "{\"status\":\"success\"}";

    }

    public static Object getCSSForUserToken(Request request, Response response) {
        String token = request.cookie(AuthController.AUTH_COOKIE);
        if (token == null) {
            return missingResource(response);
        }

        String username = AuthService.getUsernameByToken(token);
        if (username == null) {
            return forbitten(response);
        }
        response.type("text/css");

        return ProfileService.getStyleFromUser(username);
    }
    public static Object getCSSFromUsername(Request request, Response response) {
        String username = request.params("username");
        if (username == null) return missingResource(response);
        response.type(MediaType.CSS.getValue());

        return ProfileService.getStyleFromUser(username);
    }
}
