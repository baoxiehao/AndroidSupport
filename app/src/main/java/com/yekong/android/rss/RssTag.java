package com.yekong.android.rss;

import org.xml.sax.Attributes;

public class RssTag {
    private static final String CHANNEL = "channel";
    private static final String FEED = "feed";

    private static final String ITEM = "item";
    private static final String ENTRY = "entry";

    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESC = "description";
    private static final String PUB_DATE = "pubDate";
    private static final String LAST_BUILD_DATE = "lastBuildDate";
    private static final String UPDATED = "updated";

    private static final String ATTR_HREF = "href";

    public static boolean isFeed(String tag) {
        return CHANNEL.equalsIgnoreCase(tag) || FEED.equalsIgnoreCase(tag);
    }

    public static boolean isItem(String tag) {
        return ITEM.equalsIgnoreCase(tag) || ENTRY.equalsIgnoreCase(tag);
    }

    public static boolean isTitle(String tag) {
        return TITLE.equalsIgnoreCase(tag);
    }

    public static boolean isLink(String tag) {
        return LINK.equalsIgnoreCase(tag);
    }

    public static boolean isDesc(String tag) {
        return DESC.equalsIgnoreCase(tag);
    }

    public static boolean isPubDate(String tag) {
        return PUB_DATE.equalsIgnoreCase(tag) || UPDATED.equalsIgnoreCase(tag);
    }

    public static boolean isLastBuildDate(String tag) {
        return LAST_BUILD_DATE.equalsIgnoreCase(tag) || UPDATED.equalsIgnoreCase(tag);
    }

    public static String getAttrHref(Attributes attrs) {
        return attrs.getValue(ATTR_HREF);
    }
}
