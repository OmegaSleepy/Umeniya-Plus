package org.martin.util.enums;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public enum ProfileIcons {
    AGENT, BUSINESSMAN, CHEF, DICK, GIRL_NEXT_DOOR, GOBLIN,
    GRANDFATHER, HIP_HOP_BOY, INDIAN_PRINCESS, INDIAN, KNIGHT,
    LANDLADY, LITERARY_UNCLE, LOLI, PRINCESS, QUACK, SANTA_CLAUS, SCHOOL_GIRL, THIEF, UNCLE;
    public static final String PAST_FIX = "-svgrepo-com.svg";

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_","-");
    }

    public String location(){
        return super.toString().toLowerCase().replace("_","-")+PAST_FIX;
    }

    public static String getProfileStyle(String name){
        return name.toUpperCase().replace("-","_");
    }

    public static ProfileIcons getRandom(){
        int random = (int) (abs(random()*100)%ProfileIcons.values().length);
        return ProfileIcons.values()[random];
    }
}
