package omega.sleepy.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import omega.sleepy.dao.BlogDao;
import omega.sleepy.routes.ApiRoutes;
import omega.sleepy.util.Log;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

public class ApiController {

    private static JsonParser jsonParser;

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

        //data class
        Blog blog = new Blog(0, title,category,excerpt,content);

        //in /validation
        if(!isValidBlog(blog)) return "{\"status\":\"not ok\"}";

        Log.info(blog.toString());
        BlogDao.addBlog(blog);

        return "{\"status\":\"ok\"}";
    }
}
