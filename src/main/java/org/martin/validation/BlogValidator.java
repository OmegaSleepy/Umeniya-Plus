package org.martin.validation;

import org.martin.controllers.ResourceController;
import org.martin.data.Blog;

public class BlogValidator {

    public static boolean isValidBlog(Blog blog) {
        var titleL = blog.title().length();
        if(titleL > 64) return false;
        if(!ResourceController.getCategories().containsKey(blog.tag())) return false;
        if(blog.excerpt().length() > 128) return false;
        return blog.content().length() <= 8000;
    }
}
