package omega.sleepy.services;

import omega.sleepy.dao.BlogDao;
import omega.sleepy.dao.UserDao;
import omega.sleepy.data.Blog;
import omega.sleepy.data.User;
import omega.sleepy.util.BlogFilter;
import omega.sleepy.util.Log;
import omega.sleepy.util.PermittingLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static omega.sleepy.validation.BlogValidator.isValidBlog;

public class BlogService {

    public static boolean saveBlog(String title, String category, String excerpt, String content, String creator) {
        if(category.equalsIgnoreCase("Any")) category = "None";

        Blog blog = new Blog(0, title, category, excerpt, content, creator, LocalDateTime.now().toString(),0,0);

        Log.info(blog.toString());

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
        blogs.forEach(blog -> filteredBlogs.add(blog.getWithoutContents()));
        return filteredBlogs;
    }

    public static List<Blog> getBlogsByFilter(String name, String category, String order, int page) {

        BlogFilter filter = new BlogFilter.Builder()
                .direction(order)
                .title(name)
                .category(category)
                .page(page).build();

        return getBlogsByFilter(filter);
    }


    public static boolean deleteBlogById(int id) {
        return BlogDao.deleteBlogById(id);
    }

    public static boolean canEdit(int id, String username) {
        User user = ProfileService.getProfile(username);

        if(user == null) return false;

        Blog blog = BlogDao.getBlogById(id);
        if(Objects.isNull(blog)) return false;
        if(blog.creator().equals(username)) return true;

        if(user.permittingLevel() != PermittingLevel.ADMIN) return false;
        return false;
    }

    public static List<Blog> getBlogsByAuthor(String author) {
        List<Blog> blogs;
        if(!AuthService.userExists(author)) return null;

        blogs = BlogDao.getBlogByAuthor(author);
        return blogs;

    }

    private static final int GENERAL_REWARD = 10;

    public static Object award(String user, String author, int page) {
        if (!AuthService.userExists(author)) return null;
        if (!AuthService.userExists(user)) return null;
        if (user.equals(author)) return null;
        if (page < 1) return null;

        BlogDao.addOneView(page);

        if(BlogDao.hasRecordOf(user, page)) return null;

        UserDao.addFlamesToUsername(GENERAL_REWARD, user);
        UserDao.addFlamesToUsername(GENERAL_REWARD, author);

        BlogDao.addRecordOfReadBlog(user, page);

        return true;
    }
}
