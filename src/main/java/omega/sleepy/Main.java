package omega.sleepy;

import omega.sleepy.dao.BlogDao;
import omega.sleepy.routes.RouteMain;
import omega.sleepy.util.Database;
import omega.sleepy.util.Log;
import spark.Spark;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        ipAddress("0.0.0.0");
        port(4567);
        staticFileLocation("/public");

        RouteMain.init();
        BlogDao.init();
        Database.initDatabase();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Log.exec("Shutting down server...");
            Spark.stop();
        }));
    }
}