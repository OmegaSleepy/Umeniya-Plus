package omega.sleepy.services;

import omega.sleepy.dao.BlogDao;
import omega.sleepy.data.Blog;
import omega.sleepy.util.BlogFilter;
import omega.sleepy.util.Direction;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

    public static List<Blog> getBlogsByFilter(BlogFilter filter) {
        List<Blog> blogs;
        blogs = BlogDao.getBlogsByFilter(filter);
        List<Blog> filteredBlogs = new ArrayList<>();
        blogs.forEach(blog -> filteredBlogs.add(new Blog(blog.id(), blog.title(), blog.tag(), blog.excerpt(), "", blog.creator(), blog.creationDate())));
        return filteredBlogs;
    }

    public static List<Blog> getBlogsByFilter(String name, String category, String order) {

        Direction orderDirection = order.equals("oldest-first") ? Direction.ASC : Direction.DESC;

        BlogFilter filter = new BlogFilter.Builder()
                .direction(orderDirection)
                .title(name)
                .category(category).build();

        return getBlogsByFilter(filter);
    }
}
