package omega.sleepy.dao;

import java.util.List;

public class BlogDao {

    private static List<String> categories;

    public static List<String> getCategories(){
        return categories;
    }

    public static void init(){

        categories = List.of("Mathematics", "Science", "Biology", "Chemistry", "Physics", "English", "History",
                "Geography", "Art", "Music", "Computer Science", "Economics", "Philosophy",
                "Literature", "None", "Any");
    }

}
