package omega.sleepy.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.data.Blog;
import omega.sleepy.routes.ApiRoutes;
import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import org.thymeleaf.context.Context;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static omega.sleepy.routes.PublicRoutes.templateEngine;
import static omega.sleepy.validation.BlogValidator.isValidBlog;

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

    public static String saveBlog(Request request) {
        JsonObject body = jsonParser.parse(request.body()).getAsJsonObject();

        String title = body.get("title").getAsString();
        String category = body.get("category").getAsString();
        String excerpt = body.get("excerpt").getAsString();
        String content = body.get("content").getAsString();

        if(category.equalsIgnoreCase("Any")) category = "None";

        Blog blog = new Blog(0, title,category,excerpt,content, "None", LocalTime.now().toString());

        if(!isValidBlog(blog)) return "{\"status\":\"not ok\"}";

        Log.info(blog.toString());
        BlogDao.addBlog(blog);

        return "{\"status\":\"ok\"}";
    }

    public static String getBlog(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();

        model.put("id", request.params(":id"));

        try {
            Blog blog = BlogDao.getBlogById(Integer.parseInt(request.params(":id")));
            model.put("blog", blog);

            Context context = new Context();
            context.setVariables(model);

            return templateEngine.process("blog_page", context);
        } catch (Exception e) {
            response.status(404);
            response.type(MediaType.JSON.getValue());
            response.redirect("/home");
            return "{\"status\":\"error\"}";
        }
    }

    public static Object getFavicon(spark.Response response) {
        response.type("image/x-icon"); // correct MIME type
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
}
