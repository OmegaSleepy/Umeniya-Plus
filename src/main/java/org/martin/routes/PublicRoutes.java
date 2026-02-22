package org.martin.routes;


import org.martin.controllers.ApiController;
import org.martin.controllers.PublicController;
import org.martin.util.Log;
import org.martin.util.enums.MediaType;
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
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); //to true in prod
        templateEngine.setTemplateResolver(resolver);

        after((req, res) -> Log.info("Matched: " + req.pathInfo()));

        get("/", (request, response) -> getSimpleTemplate("start", response));

        get("/create", PublicController::createBlog);

        get("/home", (request, response) -> getSimpleTemplate("home_blogs", response));

        get("/blog/:id", ApiController::getBlogPageById);

        get("/404", (request, response) -> getSimpleTemplate("404", response));

        get("/login", PublicController::loginInterface);

        get("/shop", PublicController::shop);

        get("/my-likes", PublicController::myLikes);

        get("/register", PublicController::register);

        redirect.get("/signup", "/register");

        get("/dashboard", PublicController::dashboard);

        get("/edit/:id", (request, response) ->  getSimpleTemplate("edit", response));

        get("/logout", (request, response) -> getSimpleTemplate("logout", response));

        get("/user/:username", PublicController::userProfile);

        redirect.get("/me", "/dashboard");

        path("/help", () -> {
            get("/markdown-info", (request, response) -> getSimpleTemplate("markdown", response));
        });

        notFound((request, response) -> {
            response.redirect("/404");
            return null;
        });

        Log.info("All public rouses initialized");

    }

    public static String getSimpleTemplate(String pageName, Response response){
        response.type(MediaType.HTML.getValue());
        return templateEngine.process(pageName, new Context());
    }

}
