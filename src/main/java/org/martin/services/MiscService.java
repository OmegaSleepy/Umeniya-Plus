package org.martin.services;

import org.martin.controllers.ApiController;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import java.io.IOException;

import static org.martin.controllers.ControllerUtil.missingResource;

/****/
public class MiscService {

    public static Object getImage(String image, Request request, Response response) {

        try (var inputStream = ApiController.class.getResourceAsStream("/public/img/"+image)) {

            if (inputStream == null) {
                return missingResource(response);
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
