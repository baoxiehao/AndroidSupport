package com.yekong.android.rss;

import android.content.Context;

import com.jakewharton.rxrelay.ReplayRelay;
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

    private static RssFactory sInstance;

    private Context mContext;
    private ReplayRelay<Integer> mReplayNumCategory = ReplayRelay.create();
    private ReplayRelay<RssConfig.Category> mReplayCategory = ReplayRelay.create();
    private ReplayRelay<RssConfig.Feed> mReplayFeed = ReplayRelay.create();

    private RssFactory(Context context) {
        mContext = context;
        FileUtils.readAssetContent(mContext, "feeds.json")
                .map(new Func1<String, RssConfig>() {
                    @Override
                    public RssConfig call(String s) {
                        return RssConfig.fromJson(s);
                    }
                })
                .flatMap(new Func1<RssConfig, Observable<RssConfig.Feed>>() {
                    @Override
                    public Observable<RssConfig.Feed> call(RssConfig rssConfig) {
                        for (RssConfig.Category category : rssConfig.categories) {
                            mReplayCategory.call(category);
                            Logger.d(TAG, "category found: " + RssConfig.Category.toJson(category));
                        }
                        mReplayNumCategory.call(rssConfig.categories.size());
                        Logger.d(TAG, "num categories found: " + rssConfig.categories.size());
                        Logger.d(TAG, "feeds found: " + rssConfig.feeds.size());
                        return Observable.from(rssConfig.feeds);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<RssConfig.Feed>() {
                    @Override
                    public void call(RssConfig.Feed feed) {
                        mReplayFeed.call(feed);
                    }
                });
    }

    public static synchronized RssFactory getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RssFactory(context);
        }
        return sInstance;
    }

    public Observable<RssConfig.Category> categoryObservable() {
        return mReplayNumCategory.toSerialized()
                .first()
                .flatMap(new Func1<Integer, Observable<RssConfig.Category>>() {
                    @Override
                    public Observable<RssConfig.Category> call(Integer integer) {
                        return mReplayCategory.toSerialized().take(integer);
                    }
                });
    }

    public Observable<RssFeed> feedObservable(final Context context, final RssConfig.Category category) {
        return mReplayFeed.toSerialized()
                .filter(new Func1<RssConfig.Feed, Boolean>() {
                    @Override
                    public Boolean call(RssConfig.Feed feed) {
                        return category.tags.contains(feed.tag);
                    }
                })
                .flatMap(new Func1<RssConfig.Feed, Observable<RssFeed>>() {
                    @Override
                    public Observable<RssFeed> call(final RssConfig.Feed rssConfigFeed) {
                        return Observable.concat(
                                Provider.getInstance(context).restoreRssFeed(rssConfigFeed.link),
                                RssReader.parse(rssConfigFeed.link)
                                        .subscribeOn(Schedulers.io())
                                        .map(new Func1<RssHandler, RssFeed>() {
                                            @Override
                                            public RssFeed call(RssHandler rssHandler) {
                                                Logger.d(TAG, String.format("feedObservable: url=%s, size=%d",
                                                        rssConfigFeed.link, rssHandler.getFeed().entries.size()));
                                                return rssHandler.getFeed();
                                            }
                                        })
                                        .doOnNext(new Action1<RssFeed>() {
                                            @Override
                                            public void call(RssFeed rssFeed) {
                                                Provider.getInstance(mContext).saveRssFeed(rssFeed);
                                            }
                                        }))
                                .first();
                    }
                });
    }
}
