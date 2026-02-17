package omega.sleepy.services;

import omega.sleepy.controllers.ApiController;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import java.io.IOException;

import static omega.sleepy.controllers.ApiController.missingResourcePage;

public class MiscService {

    public static Object getImage(String image, Request request, Response response) {

        try (var inputStream = ApiController.class.getResourceAsStream("/public/img/"+image)) {

            if (inputStream == null) {
                return missingResourcePage(response);
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
