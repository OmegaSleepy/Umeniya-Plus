package omega.sleepy.util;

import omega.sleepy.dao.BlogDao;

public class BlogFilter {

    private final String title;
    private final String category;
    private final Direction direction;
    private final int page;

    BlogFilter(Builder builder) {
        this.title = builder.title;
        this.category = builder.category;
        this.direction = builder.direction;
        this.page = builder.page;
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

    public int getPage() {
        return page;
    }

    public static class Builder {
        private String title;
        private String category;
        private Direction direction;
        private int page;

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

        public Builder direction(String direction) {
            Direction directionValue;
            if(direction == null) direction = "";
            directionValue = switch (direction) {
                case "oldest-first" ->  Direction.DESC;
                default -> Direction.ASC;
            };
            this.direction = directionValue;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }
        public BlogFilter build() {
            title = title == null ? "" : title;
            category = category == null ? BlogDao.getDefaultCategory() : category;
            direction = direction == null ? Direction.ASC : direction;
            return new BlogFilter(this);
        }
    }


}
