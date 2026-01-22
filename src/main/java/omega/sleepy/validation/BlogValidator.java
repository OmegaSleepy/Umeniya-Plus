package omega.sleepy.validation;

import omega.sleepy.dao.BlogDao;
import omega.sleepy.data.Blog;

public class BlogValidator {

    public static boolean isValidBlog(Blog blog) {
        var titleL = blog.title().length();
        if(titleL > 64) return false;
        if(!BlogDao.getCategories().contains(blog.tag())) return false;
        if(blog.excerpt().length() > 64) return false;
        return blog.content().length() <= 8000;
    }
}
