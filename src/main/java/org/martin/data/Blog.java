package org.martin.data;

import org.jetbrains.annotations.NotNull;

public record Blog
        (int id, String title, String tag, String excerpt, String content,
         String creator, String creationDate,
         int tax, int views) {

    public boolean isNull(){
        return title == null;
    }

    @NotNull
    @Override
    public String toString() {
        return title + " " + tag + " " + excerpt + " " + content;
    }

    public Blog getWithoutContents(){
        return new Blog(id, title, tag, excerpt, "", creator, creationDate, tax, views);
    }
}

