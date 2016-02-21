package com.yekong.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by baoxiehao on 16/2/15.
 */
public class DateUtils {

    public static String normalizeDate(String date) {
        final String[] DATE_FORMATES = new String[]{
                "EEE, dd MMM yyyy HH:mm:ss",
                "EEE, dd MMM yyyy HH:mm:ss Z",
                "EEE,dd MMM yyyy HH:mm:ss Z",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.sss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy/MM/dd HH:mm:ss",
        };
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        for (String dateFormat : DATE_FORMATES) {
            try {
                return df.format(new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(date));
            } catch (ParseException e) {
                // ignore
            }
        }
        return date;
    }
}
