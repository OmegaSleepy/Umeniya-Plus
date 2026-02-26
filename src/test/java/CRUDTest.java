import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.martin.services.AuthService;
import org.martin.services.BlogService;
import org.martin.services.ProfileService;
import org.martin.util.db.Database;

import static org.junit.jupiter.api.Assertions.*;

public class CRUDTest {

    @BeforeEach
    public void setup() {
        Database.initDatabase();
    }

    @Test
    public void testCRUD() {
        String username = "username";

        assertTrue(BlogService.saveBlog("222", "Няма", "2", "2", username));
        assertFalse(BlogService.saveBlog("332", "wНяма", "2", "2", username));
        assertTrue(BlogService.saveBlog("3134", "Няма", "2", "2", username));
        assertFalse(BlogService.saveBlog("22122312", "Няsasasма", "2", "2", username));
        assertTrue(BlogService.saveBlog("22312322", "Няма", "2", "2", username));

        BlogService.getBlogsByUsername(username).forEach(blog -> assertTrue(BlogService.deleteBlogById(blog.id())));
    }

    @Test
    public void testUserCreation() {
        String username = "username";
        String password = "password";

        try {
            AuthService.createUser(username, password);
        }  catch (Exception e) {
            assertEquals("Паролата не е достатъчно силна", e.getMessage());
        }

        password = "289euwoqwU@I!33";

        try {
            AuthService.createUser(username, password);
        } catch (Exception e) {
            assertNull(e.getMessage());
        }

        ProfileService.addFunds(20000, username);
        assertEquals(20000+250, ProfileService.checkFunds(username));


        try {
            AuthService.deleteProfile(username);
        } catch (Exception e) {
            assertNull(e.getMessage());
        }
    }

    @AfterAll
    public static void cleanup() {
        String username = "username";
        AuthService.deleteProfile(username);
    }
}
