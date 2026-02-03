package omega.sleepy.util;

public enum ProfileIcons {
    AGENT, BUSINESSMAN, CHEF, DICK, GIRL_NEXT_DOOR, GOBLIN,
    GRANDFATHER, HIP_HOP_BOY, INDIAN_PRINCESS, INDIAN, KNIGHT,
    LANDLADY, LITERACY_UNCLE, LOLI, PRINCESS, QUACK, SANTA_CLAUS, SCHOOL_GIRL, THIEF, UNCLE;
    public static final String PAST_FIX = "-svgrepo-com.svg";

    @Override
    public String toString() {
        return String.join(super.toString().toLowerCase().replace("_","-"), PAST_FIX);
    }
}
