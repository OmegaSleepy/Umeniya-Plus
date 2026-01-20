package omega.sleepy.routes;


import omega.sleepy.controllers.ApiController;
import omega.sleepy.util.Log;
import omega.sleepy.util.MediaType;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import spark.Response;

import static spark.Spark.*;

public class PublicRoutes {

    public static TemplateEngine templateEngine;

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

        get("/", (request, response) -> getSimpleTemplate("start", response));

        get("/create", (request,response) -> getSimpleTemplate("create_blog", response));

        get("/home", (request, response) -> getSimpleTemplate("home_blogs", response));

        get("blog/:id", ApiController::getBlogById);

        get("/404", (request, response) -> getSimpleTemplate("404", response));

        get("/login", (request, response) -> getSimpleTemplate("login", response));

        get("/register", (request, response) -> getSimpleTemplate("register", response));

        notFound((request, response) -> {
            response.redirect("/404");
            return null;
        });

        Log.info("All public rouses initialized");

    }

    private static String getSimpleTemplate(String pageName, Response response){
        response.type(MediaType.HTML.getValue());
        return templateEngine.process(pageName, new Context());
    }


}
