package org.martin;

import org.martin.dao.BlogDao;
import org.martin.dao.UserDao;
import org.martin.routes.RouteMain;
import org.martin.util.Log;
import org.martin.util.db.Database;
import spark.Spark;

import static spark.Spark.*;

//Entry point of the application. Initializes the program
public class Main {
    public static void main(String[] args) {
        // Server config
        ipAddress("0.0.0.0");
        port(4567);
        staticFileLocation("/public");

        // Logs
        Log.purgeOldLogs();
        Log.info("Initializing System Logic");


        //Initializing BusinessLogic
        RouteMain.init();
        Database.initDatabase();
        UserDao.deleteOldTokens();

        // Shutdown hook, mostly for logging
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Log.exec("Shutting down server...");
            Spark.stop();
            Log.warn("Server shut down");
            Log.writeoutBuffer(); //saves the logs to a file
        }));
    }
}
