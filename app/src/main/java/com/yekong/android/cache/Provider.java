package com.yekong.android.cache;

import android.content.Context;

import com.jakewharton.rxrelay.ReplayRelay;
import com.yekong.android.rss.RssConfig;
import com.yekong.android.rss.RssFactory;
import com.yekong.android.rss.RssFeed;
import com.yekong.android.util.DateUtils;
import com.yekong.android.util.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.gridstone.rxstore.RxStore;
import au.com.gridstone.rxstore.converters.GsonConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by baoxiehao on 16/2/5.
 */
public class Provider {

    private static final String TAG = "Provider";

    private static Provider sInstance;

    private Context mContext;
    private Map<String, ReplayRelay<RssFeed>> mReplayRssFeeds = new HashMap<>();

    public static synchronized Provider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Provider(context);
        }
        return sInstance;
    }

    public Observable<RssFeed> feedObservable(final RssConfig.Category category) {
        return mReplayRssFeeds.get(category.name).toSerialized();
    }

    private Provider(Context context) {
        mContext = context;
        RssFactory.getInstance(context).categoryObservable()
                .doOnNext(new Action1<RssConfig.Category>() {
                    @Override
                    public void call(RssConfig.Category category) {
                        mReplayRssFeeds.put(category.name, ReplayRelay.<RssFeed>create());
                        subscribeRssFeeds(category);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RssConfig.Category>() {
                    @Override
                    public void call(RssConfig.Category category) {
                        Logger.d(TAG, RssConfig.Category.toJson(category));
                    }
                });
    }

    private void subscribeRssFeeds(final RssConfig.Category category) {
        RssFactory.getInstance(mContext).feedObservable(mContext, category)
                .startWith(restoreRssFeeds(category.name))
                .subscribeOn(Schedulers.io())
                .filter(new Func1<RssFeed, Boolean>() {
                    @Override
                    public Boolean call(RssFeed rssFeed) {
                        return rssFeed.entries.size() > 0;
                    }
                })
                .subscribe(new Action1<RssFeed>() {
                    @Override
                    public void call(RssFeed rssFeed) {
                        mReplayRssFeeds.get(category.name).call(rssFeed);
                        Logger.d(TAG, String.format("%s next: title=%s, size=%d",
                                category.name.toUpperCase(), rssFeed.title, rssFeed.entries.size()));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG, String.format("%s error", category.name.toUpperCase()), throwable);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, String.format("%s done", category.name.toUpperCase()));
                    }
                });
    }

//                        .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
//                            @Override
//                            public Observable<?> call(Observable<? extends Throwable> errors) {
//                                return errors.flatMap(new Func1<Throwable, Observable<Throwable>>() {
//                                    @Override
//                                    public Observable<Throwable> call(Throwable throwable) {
//                                        Logger.e(TAG, "retryWhen " + url + " " + throwable.getMessage());
//                                        if (throwable != null && !(throwable instanceof TimeoutException)) {
//                                            return Observable.just(null);
//                                        }
//                                        return Observable.error(throwable);
//                                    }
//                                }).zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Integer>() {
//                                    @Override
//                                    public Integer call(Throwable throwable, Integer integer) {
//                                        return integer;
//                                    }
//                                }).flatMap(new Func1<Integer, Observable<?>>() {
//                                    @Override
//                                    public Observable<?> call(Integer retryCount) {
//                                        Logger.d(TAG, String.format("Retry %s for the %d time", url, retryCount));
//                                        return Observable.timer((long) Math.pow(2, retryCount), TimeUnit.SECONDS);
//                                    }
//                                });
//                            }
//                        }))

    public void saveRssFeed(RssFeed rssFeed) {
        rssFeed.updateTime = DateUtils.getCurrentDate();
        RxStore.withContext(mContext)
                .in("rss")
                .using(new GsonConverter())
                .put("feed" + rssFeed.link.hashCode(), rssFeed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RssFeed>() {
                    @Override
                    public void call(RssFeed rssFeed) {
                        Logger.d(TAG, String.format("saveRssFeed: title=%s, size=%d",
                                rssFeed.title, rssFeed.entries.size()));
                    }
                });
    }

    public Observable<RssFeed> restoreRssFeed(final String url) {
        return RxStore.withContext(mContext)
                .in("rss")
                .using(new GsonConverter())
                .get("feed" + url.hashCode(), RssFeed.class)
                .subscribeOn(Schedulers.io())
                .filter(new Func1<RssFeed, Boolean>() {
                    @Override
                    public Boolean call(RssFeed rssFeed) {
                        boolean outdated = DateUtils.isOutdated(rssFeed.updateTime,
                                30 * android.text.format.DateUtils.SECOND_IN_MILLIS);
                        Logger.d(TAG, String.format("restoreRssFeed: url=%s, updateTime=%s, outdated=%s",
                                url, rssFeed.updateTime, outdated));
                        return !outdated;
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, String.format("restoreRssFeed: url=%s", url));
                    }
                });
    }

    public void saveRssFeeds() {
        RssFactory.getInstance(mContext).categoryObservable()
                .subscribe(new Action1<RssConfig.Category>() {
                    @Override
                    public void call(RssConfig.Category category) {
                        saveRssFeeds(category);
                    }
                });
    }

    private void saveRssFeeds(final RssConfig.Category category) {
        Logger.d(TAG, String.format("saveRssFeeds: tag=%s", category.name.toUpperCase()));
        feedObservable(category)
                .toList()
                .subscribe(new Action1<List<RssFeed>>() {
                    @Override
                    public void call(List<RssFeed> rssFeeds) {
                        RxStore.withContext(mContext)
                                .in("rss")
                                .using(new GsonConverter())
                                .putList(category.name, rssFeeds, RssFeed.class)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<List<RssFeed>>() {
                                    @Override
                                    public void call(List<RssFeed> rssFeeds) {
                                        Logger.d(TAG, "saveRssFeeds: size=" + rssFeeds.size());
                                    }
                                });
                    }
                });
    }

    private Observable<RssFeed> restoreRssFeeds(final String tag) {
        Logger.d(TAG, String.format("restoreRssFeeds: tag=%s", tag.toUpperCase()));
        return RxStore.withContext(mContext)
                .in("rss")
                .using(new GsonConverter())
                .getList(tag, RssFeed.class)
                .flatMap(new Func1<List<RssFeed>, Observable<RssFeed>>() {
                    @Override
                    public Observable<RssFeed> call(List<RssFeed> rssFeeds) {
                        return Observable.from(rssFeeds);
                    }
                });
    }
}
