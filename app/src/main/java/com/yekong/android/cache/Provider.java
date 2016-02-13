package com.yekong.android.cache;

import android.content.Context;

import com.jakewharton.rxrelay.ReplayRelay;
import com.yekong.android.rss.OpmlEntry;
import com.yekong.android.rss.OpmlHandler;
import com.yekong.android.rss.OpmlReader;
import com.yekong.android.rss.RssEntry;
import com.yekong.android.rss.RssHandler;
import com.yekong.android.rss.RssReader;
import com.yekong.android.util.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private ReplayRelay<List<RssEntry>> mReplayRelay = ReplayRelay.create();

    public static synchronized Provider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Provider(context);
        }
        return sInstance;
    }

    public Observable<List<RssEntry>> allEntries() {
        return mReplayRelay.toSerialized();
    }

    private Provider(Context context) {
        mContext = context;
        parseOpml()
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<RssEntry>>() {
                    @Override
                    public void call(List<RssEntry> rssEntries) {
                        Logger.d(TAG, "parseOpml next: " + rssEntries.size());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG, "parseOpml error: ", throwable);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, "parseOpml done");
                    }
                });
    }

    private Observable<List<RssEntry>> parseOpml() {
        return OpmlReader.parse(mContext, "opml.xml")
                .subscribeOn(Schedulers.io())
                .concatMap(new Func1<OpmlHandler, Observable<OpmlEntry>>() {
                    @Override
                    public Observable<OpmlEntry> call(OpmlHandler opmlHandler) {
                        Logger.d(TAG, "parseOpml(): " + opmlHandler.getEntries().size());
                        return Observable.from(opmlHandler.getEntries());
                    }
                })
                .filter(new Func1<OpmlEntry, Boolean>() {
                    @Override
                    public Boolean call(OpmlEntry opmlEntry) {
                        return opmlEntry.getXmlUrl() != null;
                    }
                })
                .concatMap(new Func1<OpmlEntry, Observable<List<RssEntry>>>() {
                    @Override
                    public Observable<List<RssEntry>> call(OpmlEntry opmlEntry) {
                        return parseRssEntry(opmlEntry.getXmlUrl());
                    }
                })
                .filter(new Func1<List<RssEntry>, Boolean>() {
                    @Override
                    public Boolean call(List<RssEntry> rssEntries) {
                        return rssEntries != null;
                    }
                })
                .doOnNext(new Action1<List<RssEntry>>() {
                    @Override
                    public void call(List<RssEntry> rssEntries) {
                        mReplayRelay.call(rssEntries);
                    }
                });
    }

    private Observable<List<RssEntry>> parseRssEntry(String url) {
        return Observable.concat(
                restoreRssEntries(url),
                RssReader.parse(url)
                        .flatMap(new Func1<RssHandler, Observable<List<RssEntry>>>() {
                            @Override
                            public Observable<List<RssEntry>> call(RssHandler rssHandler) {
                                saveRssEntries(rssHandler);
                                return Observable.just(rssHandler.getEntries());
                            }
                        })
                        .timeout(1, TimeUnit.SECONDS)
                        .onErrorReturn(new Func1<Throwable, List<RssEntry>>() {
                            @Override
                            public List<RssEntry> call(Throwable throwable) {
                                return null;
                            }
                        }))
                .first();
    }

    private void saveRssEntries(RssHandler rssHandler) {
        RxStore.withContext(mContext)
                .in("rss")
                .using(new GsonConverter())
                .putList("feed" + rssHandler.getUrl().hashCode(), rssHandler.getEntries(), RssEntry.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<RssEntry>>() {
                    @Override
                    public void call(List<RssEntry> rssEntries) {
                        Logger.d(TAG, "saveRssEntries: " + rssEntries.size());
                    }
                });
    }

    private Observable<List<RssEntry>> restoreRssEntries(String url) {
        Logger.d(TAG, "restoreRssEntries: " + url);
        return RxStore.withContext(mContext)
                .in("rss")
                .using(new GsonConverter())
                .getList("feed" + url.hashCode(), RssEntry.class);
    }
}
