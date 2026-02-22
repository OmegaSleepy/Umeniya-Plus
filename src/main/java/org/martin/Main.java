package org.martin;

import org.martin.dao.BlogDao;
import org.martin.dao.UserDao;
import org.martin.routes.RouteMain;
import org.martin.util.db.Database;
import org.martin.util.Log;
import spark.Spark;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        ipAddress("0.0.0.0");
        port(4567);
        staticFileLocation("/public");

        Log.info("Initializing System Logic");

        RouteMain.init();
        BlogDao.init();
        Database.initDatabase();
        UserDao.deleteOldTokens();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Log.exec("Shutting down server...");
            Spark.stop();
        }));
    }
}
