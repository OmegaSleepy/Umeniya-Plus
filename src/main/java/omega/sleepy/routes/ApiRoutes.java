package omega.sleepy.routes;

import omega.sleepy.controllers.ApiController;
import omega.sleepy.controllers.AuthController;
import omega.sleepy.controllers.ResourceController;

import static spark.Spark.*;

public class ApiRoutes {
    public static void init() {

        get("/api/style/:styleSheet", ResourceController::getStyleSheet);
        get("/api/js/:js", ResourceController::getJavaScriptFile);
        get("/api/image/:image", ResourceController::getImage);


        get("/favicon.ico", ResourceController::getFavicon);
        get("/favicon-logo.ico", ResourceController::getFavicon);
        get("/api/profile-icon/:icon", ResourceController::getIcon);


        get("/api/user/me-info", ApiController::getUserInformation);
        get("/api/user/:user", ApiController::getSpecificUserInformation);

        get("/api/icons", ResourceController::getIcons);
        get("/api/user/change-pfp/:icon", AuthController::changePfp);

        get("/api/variables-css/", ResourceController::getCssVars);

        post("/api/create-css/", ApiController::createCSSForUser);


        path("/api/blog", () -> {

            post("/create", ApiController::createBlog);

            get("/get-filtered-view/", ApiController::getFilteredView);
            get("/by-author/:author", ApiController::getByAuthor);

            post("/read", ApiController::rewardReader);

            get("/tags", ResourceController::getCategories);

            get("/:id", ApiController::getBlogContentsById);

            get("/full/:id", ApiController::getBlogById);

            get("/can-edit/:id", ApiController::checkCanEdit);
            post("/delete/:id", ApiController::deleteBlog);
            post("/edit/:id", ApiController::editBlog);

            put("/update/:id", ApiController::updateBlog);

        });

    }

}
