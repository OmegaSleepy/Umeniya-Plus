package org.martin.routes;

import org.martin.controllers.ApiController;
import org.martin.controllers.AuthController;
import org.martin.controllers.ResourceController;

import static spark.Spark.*;

/**
 * All API requests end here
 * @see ApiController
 * @see org.martin.services.BlogService
 * @see org.martin.services.MiscService
 * **/
public class ApiRoutes {
    public static void init() {

        get("/api/style/:styleSheet", ResourceController::getStyleSheet);
        get("/api/js/:js", ResourceController::getJavaScriptFile);
        get("/api/image/:image", ResourceController::getImage);


        get("/favicon.ico", ResourceController::getFavicon);
        get("/favicon-logo.ico", ResourceController::getFavicon);
        get("/api/profile-icon/:icon", ResourceController::getIcon);


        get("/api/icons", ResourceController::getIcons);

        get("/api/variables-css/", ResourceController::getCssVars);

        post("/api/create-css/", ApiController::createCSSForUser);
        get("/api/style-for/me", ApiController::getCSSForUserToken);
        get("/api/style-for-someone/:username", ApiController::getCSSFromUsername);

        path("/api/user", () -> {
            get("/me-info", ApiController::getUserInformation);
            get("/my-likes", ApiController::getLikedPosts);
            get("/total-views/:user", ApiController::getTotalViewsByAuthor);
            get("/total-likes/:user", ApiController::getTotalLikesByAuthor);
            get("/change-pfp/:icon", AuthController::changePfp);
            get("/:user", ApiController::getSpecificUserInformation);
        });


        path("/api/blog", () -> {

            post("/create", ApiController::createBlog);

            get("/get-filtered-view/", ApiController::getFilteredView);
            get("/by-author/:author", ApiController::getByAuthor);

            post("/read", ApiController::rewardReader);
            post("/like", ApiController::likePost);

            get("/tags", ResourceController::getCategories);

            get("/:id", ApiController::getBlogContentsById);

            get("/full/:id", ApiController::getBlogById);

            get("/can-edit/:id", ApiController::checkCanEdit);
            delete("/:id", ApiController::deleteBlog);
            post("/edit/:id", ApiController::editBlog);

            put("/update/:id", ApiController::updateBlog);

        });

    }

}
