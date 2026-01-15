package omega.sleepy.routes;


import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class PublicRoutes {

    private static TemplateEngine templateEngine;

    public static void init(){
        Log.info("Public routes initializing...");

        templateEngine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");   // път в resources
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); // за разработка: true в продукция
        templateEngine.setTemplateResolver(resolver);

        get("/", ((request, response) -> {
            response.type(MediaType.HTML.getValue());
            return templateEngine.process("start", new Context());
        }));

        get("/create", (req,res) ->{

            res.type(MediaType.HTML.getValue());
            return templateEngine.process("create_blog", new Context());

        });

        get("/home", ((request, response) -> {
            response.type(MediaType.HTML.getValue());
            return templateEngine.process("home_blogs", new Context());
        }));

        get("blog/:id", (request, response) -> {
            response.type(MediaType.HTML.getValue());

            Map<String, Object> model = new HashMap<>();

            model.put("id", request.params(":id"));

            return "w";

            //this is business logic,

            /*
            try {
                Blog blog = DBUtil.getBlogById(Integer.parseInt(request.params(":id")));
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
            */

        });

        Log.info("All public rouses initialized");
    }
}
