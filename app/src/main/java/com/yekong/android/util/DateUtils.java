package com.yekong.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by baoxiehao on 16/2/15.
 */
public class DateUtils {

    private static final String TAG = "DateUtils";

//    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private static final String[] DATE_FORMATES = new String[]{
            "EEE, dd MMM yyyy HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "EEE,dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.sss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
    };

    public static String normalizeDate(String date) {
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        for (String dateFormat : DATE_FORMATES) {
            try {
                return SDF.format(new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(date));
            } catch (ParseException e) {
                // ignore
            }
        }
        return date;
    }

    public static boolean isOutdated(String normalizedDate, long timeSpanInMillis) {
        return compareNormalizedDate(getCurrentDate(), normalizedDate) > timeSpanInMillis;
    }

    private static long compareNormalizedDate(String normalizedDate1, String normalizedDate2) {
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        try {
            long time1 = SDF.parse(normalizedDate1).getTime();
            long time2 = SDF.parse(normalizedDate2).getTime();
            return time2 - time1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String getCurrentDate() {
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        return SDF.format(date);
    }
}
