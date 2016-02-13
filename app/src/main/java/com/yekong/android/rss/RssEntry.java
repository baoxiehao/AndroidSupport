package com.yekong.android.rss;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RssEntry {

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
        pubDate = pubDate.replaceAll(".000Z", "");
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        try {
            this.pubDate = df.format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(pubDate));
        } catch (ParseException e) {
            try {
                this.pubDate = df.format(new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH).parse(pubDate));
            } catch (ParseException e2) {
                this.pubDate = pubDate;
            }
        }
    }

    @Override
    public String toString() {
        return sGson.toJson(this);
    }

    public static RssEntry fromJson(String json) {
        return sGson.fromJson(json, RssEntry.class);
    }
}
