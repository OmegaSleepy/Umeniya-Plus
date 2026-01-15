package omega.sleepy.data;

import org.jetbrains.annotations.NotNull;

public record Blog (int id, String title, String tag, String excerpt, String content, String creator, String creationDate) {

    @NotNull
    @Override
    public String toString() {
        return title + " " + tag + " " + excerpt + " " + content;
    }
}

