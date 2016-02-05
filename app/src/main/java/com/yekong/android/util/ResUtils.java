package com.yekong.android.util;

import com.yekong.android.R;

import java.util.Random;

/**
 * Created by baoxiehao on 16/2/5.
 */
public class ResUtils {
    private static final Random RANDOM = new Random();

    private static final int[] DRAWABLE_IDS = new int[] {
            R.drawable.app_bar_01,
            R.drawable.app_bar_02,
            R.drawable.app_bar_03,
            R.drawable.app_bar_04,
            R.drawable.app_bar_05,
            R.drawable.app_bar_06,
    };

    public static int getAppBarDrawableId() {
        return DRAWABLE_IDS[RANDOM.nextInt(DRAWABLE_IDS.length)];
    }

}
