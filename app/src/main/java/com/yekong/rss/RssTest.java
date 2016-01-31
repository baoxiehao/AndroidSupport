package com.yekong.rss;


import java.util.Random;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RssTest {

    private static final Random RANDOM = new Random();

    private static final String[] URLS = new String[] {
            "http://www.androiddesignpatterns.com/feed.atom",
            "http://www.androidweekly.cn/rss/",
            "http://likaiwen.cn/feed",
            "http://www.ruanyifeng.com/blog/atom.xml",
            "http://ticktick.blog.51cto.com/rss.php?uid=823160",
            "http://droidyue.com/atom.xml",
            "http://antonioleiva.com/feed/",
            "http://drakeet.me/feed",
            "http://feeds.feedburner.com/StylingAndroid",
            "http://andrewliu.in/atom.xml",
            "http://veaer.com/atom.xml",
            "http://blog.bihe0832.com/pages/atom.xml",
            "http://waylenw.github.io/atom.xml",
            "http://yanghui.name/atom.xml",
    };

    public static String getRandomRss() {
        return URLS[RANDOM.nextInt(URLS.length)];
    }


    public static void main(String[] args) {
        parseRss(getRandomRss());
    }

    private static void parseRss(String url) {
        RssReader.parse(url)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<RssHandler>() {
                    @Override
                    public void call(RssHandler rssHandler) {
                        System.out.println(rssHandler.getFeed());
                        for (int i = 0; i < rssHandler.getEntries().size(); i++) {
                            RssEntry item = rssHandler.getEntries().get(i);
                            System.out.println(item);
                        }
                    }
                });
    }
}
