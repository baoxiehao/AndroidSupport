package com.yekong.android.mvp;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.yekong.android.cache.Provider;
import com.yekong.android.rss.RssEntry;
import com.yekong.android.rss.RssFeed;
import com.yekong.android.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by baoxiehao on 16/1/29.
 */
public class MainListPresenter extends MvpBasePresenter<MainListView> {

    private static final String TAG = "MainListPresenter";

    public MainListPresenter() {
    }

    @Override
    public void attachView(MainListView view) {
        super.attachView(view);
        Logger.d(TAG, "attach main list");
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        Logger.d(TAG, "detach main list");
    }

    public void saveData(final Context context) {
        Provider.getInstance(context).saveAllRssFeeds();
    }

    public void loadMainList(final Context context, final boolean pullToRefresh) {
        Provider.getInstance(context)
                .allFeeds()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, "sub: " + pullToRefresh);
                        if (isViewAttached()) {
                            getView().setData(new ArrayList<RssEntry>());
                        }
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, "unsub: " + pullToRefresh);
                    }
                })
                .flatMap(new Func1<RssFeed, Observable<List<RssEntry>>>() {
                    @Override
                    public Observable<List<RssEntry>> call(RssFeed rssFeed) {
                        return Observable.just(rssFeed.entries);
                    }
                })
                .scan(new Func2<List<RssEntry>, List<RssEntry>, List<RssEntry>>() {
                    @Override
                    public List<RssEntry> call(List<RssEntry> rssEntries, List<RssEntry> rssEntries2) {
                        List<RssEntry> mergedEntries = new ArrayList<RssEntry>();
                        mergedEntries.addAll(rssEntries);
                        mergedEntries.addAll(rssEntries2);
                        return mergedEntries;
                    }
                })
                .map(new Func1<List<RssEntry>, List<RssEntry>>() {
                    @Override
                    public List<RssEntry> call(List<RssEntry> rssEntries) {
                        Collections.sort(rssEntries);
                        return rssEntries;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<RssEntry>>() {
                    @Override
                    public void call(List<RssEntry> rssEntries) {
                        if (isViewAttached()) {
                            getView().setData(rssEntries);
                            getView().showContent();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (isViewAttached()) {
                            getView().showError(throwable, pullToRefresh);
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (isViewAttached()) {
                            getView().showContent();
                        }
                    }
                });
    }
}
