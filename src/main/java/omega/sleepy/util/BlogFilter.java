package omega.sleepy.util;

import omega.sleepy.dao.BlogDao;

public class BlogFilter {

    private final String title;
    private final String category;
    private final Direction direction;

    BlogFilter(Builder builder) {
        this.title = builder.title;
        this.category = builder.category;
        this.direction = builder.direction;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public Direction getDirection() {
        return direction;
    }

    public static class Builder {
        private String title;
        private String category;
        private Direction direction;

        public Builder title(String title) {
            if (title == null) title = "";
            this.title = title;
            return this;
        }
        public Builder category(String category) {
            if (category == null) category = BlogDao.getDefaultCategory();
            this.category = category;
            return this;
        }
        public Builder direction(Direction direction) {
            if (direction == null) direction = Direction.ASC;
            this.direction = direction;
            return this;
        }
        public BlogFilter build() {
            return new BlogFilter(this);
        }
    }


}
