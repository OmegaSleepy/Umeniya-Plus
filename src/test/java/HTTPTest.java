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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HTTPTest {

    @BeforeAll
    void setup() {
        Main.main(null);
    }

    @Test
    void loadFrontPage(){
        test("", 200, "Homepage should return 200 Ok");
    }

    @Test
    void restrictedPage(){
        test("dashboard", 302, "Dashboard should be restricted");
    }

    @Test
    void fourOFour(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/3ughe9igb1i3hbe,"))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertTrue(response.headers().map().containsValue(List.of("http://localhost:4567/404")), "Random should return 302");
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

    @AfterAll
    void tearDown() {
        Spark.stop();
    }
}
