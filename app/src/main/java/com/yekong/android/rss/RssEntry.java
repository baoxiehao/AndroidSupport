package com.yekong.android.rss;

import com.google.gson.Gson;
import com.yekong.android.util.DateUtils;

public class RssEntry implements Comparable<RssEntry> {

    private static final Gson sGson = new Gson();

    String source;
    String title;
    String link;
    String description;
    String pubDate;

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = DateUtils.normalizeDate(pubDate);
    }

    @Override
    public String toString() {
        return sGson.toJson(this);
    }

    public static RssEntry fromJson(String json) {
        return sGson.fromJson(json, RssEntry.class);
    }

    @Override
    public int compareTo(RssEntry another) {
        return another.pubDate.compareTo(this.pubDate);
    }
}
