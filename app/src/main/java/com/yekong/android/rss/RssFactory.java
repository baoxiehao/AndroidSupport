package com.yekong.android.rss;

import android.content.Context;

import com.yekong.android.cache.Provider;
import com.yekong.android.util.FileUtils;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by baoxiehao on 16/2/20.
 */
public class RssFactory {

    public static Observable<RssFeed> parseRssFeed(final Context context) {
        return FileUtils.readAssetLines(context, "rss.txt")
                .flatMap(new Func1<String, Observable<RssFeed>>() {
                    @Override
                    public Observable<RssFeed> call(String url) {
                        return Observable.concat(
                                Provider.getInstance(context).restoreRssFeed(url),
                                RssReader.parse(url)
                                        .subscribeOn(Schedulers.io())
                                        .map(new Func1<RssHandler, RssFeed>() {
                                            @Override
                                            public RssFeed call(RssHandler rssHandler) {
                                                return rssHandler.getFeed();
                                            }
                                        })
                                        .doOnNext(new Action1<RssFeed>() {
                                            @Override
                                            public void call(RssFeed rssFeed) {
                                                Provider.getInstance(context).saveRssFeed(rssFeed);
                                            }
                                        }))
                                .first();
                    }
                });
    }
}
