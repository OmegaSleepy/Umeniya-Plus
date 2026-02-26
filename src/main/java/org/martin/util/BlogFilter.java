package org.martin.util;

import org.martin.controllers.ResourceController;
import org.martin.util.enums.Direction;

// Object class for
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
            if (category == null) category = ResourceController.getDefaultCategory();
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
                case "oldest-first" ->  Direction.ASC;
                case "liked-first" ->  Direction.LIKES;
                case "viewed-first" ->  Direction.VIEWS;
                default -> Direction.DESC;
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
            category = category == null ? ResourceController.getDefaultCategory() : category;
            direction = direction == null ? Direction.ASC : direction;
            return new BlogFilter(this);
        }
    }

    @Override
    public String toString() {
        return "BlogFilter{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", direction=" + direction +
                ", page=" + page +
                '}';
    }
}
