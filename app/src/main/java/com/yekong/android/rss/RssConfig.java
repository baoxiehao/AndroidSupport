package com.yekong.android.rss;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by baoxiehao on 16/2/22.
 */
public class RssConfig {
    private static final Gson sGson = new Gson();

    List<Category> categories;
    List<Feed> feeds;

    public static class Category {
        public String name;
        public List<String> tags;

        public static String toJson(Category category) {
            return sGson.toJson(category);
        }

        public static Category fromJson(String json) {
            return sGson.fromJson(json, Category.class);
        }
    }

    public static class Feed {
        public String tag;
        public String title;
        public String link;

        public static String toJson(Feed feed) {
            return sGson.toJson(feed);
        }

        public static Feed fromJson(String json) {
            return sGson.fromJson(json, Feed.class);
        }
    }

    public static String toJson(RssConfig rssConfig) {
        return sGson.toJson(rssConfig);
    }

    public static RssConfig fromJson(String json) {
        return sGson.fromJson(json, RssConfig.class);
    }
}
