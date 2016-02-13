package com.yekong.android.rss;

import com.google.gson.Gson;

/**
 * Created by baoxiehao on 16/2/4.
 */
public class OpmlEntry {
    private static final Gson sGson = new Gson();

    public String text;
    public String title;
    public String type;
    public String xmlUrl;
    public String htmlUrl;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXmlUrl() {
        return xmlUrl;
    }

    public void setXmlUrl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    @Override
    public String toString() {
        return sGson.toJson(this);
    }
}
