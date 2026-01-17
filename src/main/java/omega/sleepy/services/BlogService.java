package omega.sleepy.services;

import omega.sleepy.dao.BlogDao;
import omega.sleepy.data.Blog;

import java.time.LocalTime;
import java.util.Objects;

import static omega.sleepy.validation.BlogValidator.isValidBlog;

public class BlogService {

    public static boolean saveBlog(String title, String category, String excerpt, String content) {
        if(category.equalsIgnoreCase("Any")) category = "None";

        Blog blog = new Blog(0, title,category,excerpt,content, "None", LocalTime.now().toString());

        if(isValidBlog(blog)) {
            BlogDao.addBlog(blog);
            return true;
        }
        return false;

    }


    public static Blog getBlogById(int id) {
        return BlogDao.getBlogById(id);
    }

    public static String getBlogBodyById(int id) {
        Blog blog = BlogDao.getBlogById(id);
        if(Objects.isNull(blog)) return null;
        return (Objects.requireNonNull(BlogDao.getBlogById(id))).content();
    }
}
