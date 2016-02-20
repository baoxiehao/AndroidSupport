package com.yekong.android.rss;

import com.google.gson.Gson;

import java.util.List;

public class RssFeed {

    private static final Gson sGson = new Gson();

    public String title;
    public String link;
    public String description;
    public String lastBuildDate;
    public List<RssEntry> entries;

    @Override
    public int hashCode() {
        return link.hashCode() + 13 * lastBuildDate.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RssFeed)) {
            return false;
        }
        return toString().hashCode() == o.toString().hashCode();
    }

    @Override
    public String toString() {
        return sGson.toJson(this);
    }

    public static RssFeed fromJson(String json) {
        return sGson.fromJson(json, RssFeed.class);
    }
}
