package com.yekong.android.cache;

import android.content.Context;

import com.jakewharton.rxrelay.ReplayRelay;
import com.yekong.android.rss.RssFactory;
import com.yekong.android.rss.RssFeed;
import com.yekong.android.util.Logger;

import java.util.List;

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
    private ReplayRelay<RssFeed> mReplayRelay = ReplayRelay.create();

    public static synchronized Provider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Provider(context);
        }
        return sInstance;
    }

    public Observable<RssFeed> allFeeds() {
        return mReplayRelay.toSerialized();
    }

    private Provider(Context context) {
        mContext = context;
        RssFactory.parseRssFeed(context)
                .startWith(restoreAllRssFeeds())
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
                        mReplayRelay.call(rssFeed);
                        Logger.d(TAG, String.format("RssFeed next: title=%s, size=%d",
                                rssFeed.title, rssFeed.entries.size()));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG, "RssFeed error", throwable);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, "RssFeed done");
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
        Logger.d(TAG, String.format("restoreRssFeed: url=%s", url));
        return RxStore.withContext(mContext)
                .in("rss")
                .using(new GsonConverter())
                .get("feed" + url.hashCode(), RssFeed.class)
                .subscribeOn(Schedulers.io());
    }

    public void saveAllRssFeeds() {
        mReplayRelay.toSerialized().toList()
                .subscribe(new Action1<List<RssFeed>>() {
                    @Override
                    public void call(List<RssFeed> rssFeeds) {
                        RxStore.withContext(mContext)
                                .in("rss")
                                .using(new GsonConverter())
                                .putList("allFeeds", rssFeeds, RssFeed.class)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<List<RssFeed>>() {
                                    @Override
                                    public void call(List<RssFeed> rssFeeds) {
                                        Logger.d(TAG, "saveAllRssFeeds: size=" + rssFeeds.size());
                                    }
                                });
                    }
                });
    }

    public Observable<RssFeed> restoreAllRssFeeds() {
        Logger.d(TAG, "restoreAllRssFeeds");
        return RxStore.withContext(mContext)
                .in("rss")
                .using(new GsonConverter())
                .getList("allFeeds", RssFeed.class)
                .flatMap(new Func1<List<RssFeed>, Observable<RssFeed>>() {
                    @Override
                    public Observable<RssFeed> call(List<RssFeed> rssFeeds) {
                        return Observable.from(rssFeeds);
                    }
                });
    }
}
