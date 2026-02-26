import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.martin.Main;
import spark.Spark;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiTest {
    @BeforeAll
    void setup() {
        Main.main(null);
        Spark.awaitInitialization();
    }

    @AfterAll
    void teardown() {
        Spark.stop();
    }

    @Test
    void canEdit(){
        test("api/blog/can-edit/1", 404, "Should be 404, no cookie");
    }

    @Test
    void categories(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/"+"api/blog/tags"))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals("{\"Математика\":\"#DCEBFF\",\"Наука\":\"#D9F4F1\",\"Биология\":\"#E3F6E8\",\"Химия\":\"#EFE3FF\",\"Физика\":\"#E1F0FF\",\"Английски език\":\"#FFE3E3\",\"История\":\"#F5EAD6\",\"География\":\"#EEF3D9\",\"Изкуство\":\"#FFEBD6\",\"Музика\":\"#F2E6FF\",\"Компютърни науки\":\"#E3EAF5\",\"Икономика\":\"#E6F7F1\",\"Философия\":\"#ECE9F4\",\"Литература\":\"#FFF4E1\",\"Няма\":\"#F2F2F2\",\"Всякакви\":\"#F2F2F2\"}", response.body());
    }

    @Test
    void invalidImage(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/"+"api/image/somethingThatDoesNotExist"))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(404, response.statusCode());
    }


    void test(String url, int code, String message){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/"+url))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(code, response.statusCode(), message);
    }
}
