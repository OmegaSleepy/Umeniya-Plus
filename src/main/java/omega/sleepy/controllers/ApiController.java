package omega.sleepy.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.data.Blog;
import omega.sleepy.routes.ApiRoutes;
import omega.sleepy.services.BlogService;
import omega.sleepy.util.Direction;
import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static omega.sleepy.routes.PublicRoutes.templateEngine;

public class ApiController {

    private static JsonParser jsonParser = new JsonParser();

    public static String getStyleSheet(Response response) {
        try (var inputStream = ApiRoutes.class.getResourceAsStream("/public/css/umeniyaStyleSheet.css")) {
            if (inputStream == null) {
                response.status(404);
                return "/* CSS file not found in classpath */";
            }
            return IOUtils.toString(inputStream);
        } catch (Exception e) {
            response.status(500);
            return "/* Server Error loading CSS */";
        }
    }

    public static Object getFavicon(Response response) {
        response.type(MediaType.ICON.getValue());
        response.header("Cache-Control", "public, max-age=604800"); // 1 week

        try (var inputStream = ApiController.class.getResourceAsStream("/public/img/favicon.ico")) {
            if (inputStream == null) {
                response.status(404);
                return "";
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


    public static String createBlog(Request request) {
        JsonObject body = jsonParser.parse(request.body()).getAsJsonObject();

        String title = body.get("title").getAsString();
        String category = body.get("category").getAsString();
        String excerpt = body.get("excerpt").getAsString();
        String content = body.get("content").getAsString();

        boolean success = BlogService.saveBlog(title, category, excerpt, content);

        return success ? "{\"status\":\"ok\"}" : "{\"status\":\"not ok\"}";
    }

    public static String getBlogById(Request request, Response response) {
        int id = Integer.parseInt(request.params(":id"));
        Blog blog = BlogService.getBlogById(id);

        if(blog.isNull()) {
            response.status(404);
            response.type(MediaType.JSON.getValue());
            response.redirect("/404");
            return "{\"status\":\"error\"}";
        }

        Map<String, Object> model = new HashMap<>();
        model.put("id", request.params(":id"));
        model.put("blog", blog);

        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process("blog_page", context);

    }


    public static String getBlogContentsById(Request request, Response response) {
        var id = Integer.parseInt(request.params(":id"));
        String body = BlogService.getBlogBodyById(id);

        if(body == null) {
            response.status(404);
            response.type(MediaType.JSON.getValue());
            response.redirect("/home");
            return "{\"status\":\"error\"}";
        }

        return body;
    }

    public static Object getFilteredView(Request request, Response response) {
        String category = request.queryParams("category");
        String name = request.queryParams("name");
        String order = request.queryParams("order");

        Direction orderDirection = order.equals("oldest-first") ? Direction.ASC : Direction.DESC;

        Log.exec("Queried for " + category + " '" + name + "'");

        if(name == null) name = "";

        if(category == null){
            return BlogDao.getBlogWithoutContents();
        }

        if(BlogDao.getCategories().contains(category) && name.length()<32){
            return BlogDao.getBlogsByCategory(category, name, orderDirection);
        }

        response.status(400);
        return "Error";
    }
}
