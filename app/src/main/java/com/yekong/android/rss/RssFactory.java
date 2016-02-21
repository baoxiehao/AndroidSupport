package com.yekong.android.rss;

import android.content.Context;

import com.yekong.android.cache.Provider;
import com.yekong.android.util.FileUtils;
import com.yekong.android.util.Logger;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by baoxiehao on 16/2/20.
 */
public class RssFactory {

    private static final String TAG = "RssFactory";

    public static Observable<RssFeed> parseRssFeed(final Context context, final String assetFileName) {
        return FileUtils.readAssetLines(context, assetFileName)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !s.startsWith("#");
                    }
                })
                .flatMap(new Func1<String, Observable<RssFeed>>() {
                    @Override
                    public Observable<RssFeed> call(final String url) {
                        Logger.d(TAG, String.format("parseRssFeed: url=%s...", url));
                        return Observable.concat(
//                                Provider.getInstance(context).restoreRssFeed(url),
                                Observable.<RssFeed>empty(),
                                RssReader.parse(url)
                                        .subscribeOn(Schedulers.io())
                                        .map(new Func1<RssHandler, RssFeed>() {
                                            @Override
                                            public RssFeed call(RssHandler rssHandler) {
                                                Logger.d(TAG, String.format("parseRssFeed: url=%s, size=%d", url, rssHandler.getFeed().entries.size()));
                                                return rssHandler.getFeed();
                                            }
                                        })
                                        .doOnNext(new Action1<RssFeed>() {
                                            @Override
                                            public void call(RssFeed rssFeed) {
                                                Provider.getInstance(context).saveRssFeed(rssFeed);
                                            }
                                        }))
//                                .filter(new Func1<RssFeed, Boolean>() {
//                                    @Override
//                                    public Boolean call(RssFeed rssFeed) {
//                                        boolean result = DateUtils.isOutdated(rssFeed.lastBuildDate,
//                                                30 * android.text.format.DateUtils.SECOND_IN_MILLIS);
//                                        Logger.d(TAG, String.format("parseRssFeed: url=%s, date=%s, outdated=%s", url, rssFeed.lastBuildDate, result));
//                                        return !result;
//                                    }
//                                })
                                .first();
                    }
                });
    }
}
