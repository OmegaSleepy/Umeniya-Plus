package omega.sleepy.util;

public enum MediaType {

    JSON("application/json"),
    PNG("image/png"),
    JPG("image/jpeg"),
    SVG("imagee/svg+xml"),
    MP3("audio/mpeg"),
    WAV("audio/wav"),
    OGG("audio/ogg"),
    MP4("video/mp4"),
    WEBM("video/webm"),
    ZIP("application/zio"),
    RAR("application/vnd.rar"),
    SEVEN_Z("application/x-7z-compressed"),
    ICON("image/x-icon"),
    TXT("text/plain"),
    HTML("text/html; charset=UTF-8"),
    CSS("text/css"),
    CSV("text/csv"),
    PDF("application/pdf"),
    EXE("application/octet-stream");

    final String value;
    MediaType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
