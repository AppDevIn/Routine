package com.mad.p03.np2020.routine.helpers;

import com.mad.p03.np2020.routine.R;

import java.util.ArrayList;
import java.util.List;

public enum HomeIcon {
    AMAZON(0, R.drawable.amazon),
    ANDROID(1, R.drawable.android),
    LAPTOP(2, R.drawable.laptop),
    CODE(3, R.drawable.code),
    BOOKMARK(4, R.drawable.bookmark);

    public final int value;
    public final int backgroundURL;

    private HomeIcon(int value, int backgroundURL) {
        this.value = value;
        this.backgroundURL = backgroundURL;
    }

    public static List<Integer> getAllBackgrounds(){
        List<Integer> lst = new ArrayList<>();
        for (HomeIcon e:
             values()) {
            lst.add(e.backgroundURL);
        }

        return lst;
    }

    public static Integer getBackground(int value){

        for (HomeIcon e:
                values()) {
            if(e.value == value){
                return e.backgroundURL;
            }
        }

        return HomeIcon.AMAZON.backgroundURL;
    }

    public static int getValue(int url){

        for (HomeIcon e:
                values()) {
            if(e.backgroundURL == url){
                return e.value;
            }
        }

        return 0;
    }


}
