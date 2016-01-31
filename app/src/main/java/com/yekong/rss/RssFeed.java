package com.yekong.rss;

import com.google.gson.Gson;

public class RssFeed {

    private static final Gson sGson = new Gson();

    public String title;
    public String link;
    public String description;
    public String lastBuildDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    @Override
    public String toString() {
        return sGson.toJson(this);
    }

    public static RssFeed fromJson(String json) {
        return sGson.fromJson(json, RssFeed.class);
    }
}
