package com.yekong.android.mvp;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.yekong.android.util.Logger;
import com.yekong.rss.OpmlEntry;
import com.yekong.rss.OpmlHandler;
import com.yekong.rss.OpmlReader;
import com.yekong.rss.RssHandler;
import com.yekong.rss.RssReader;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    public void loadMainList(final Context context, final boolean pullToRefresh) {
        OpmlReader.parse(context, "opml.xml")
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<OpmlHandler, Observable<OpmlEntry>>() {
                    @Override
                    public Observable<OpmlEntry> call(OpmlHandler opmlHandler) {
                        Logger.i(TAG, "opml size: " + opmlHandler.getEntries().size());
                        return Observable.from(opmlHandler.getEntries());
                    }
                })
                .filter(new Func1<OpmlEntry, Boolean>() {
                    @Override
                    public Boolean call(OpmlEntry opmlEntry) {
                        return opmlEntry.getXmlUrl() != null;
                    }
                })
                .last()
                .flatMap(new Func1<OpmlEntry, Observable<RssHandler>>() {
                    @Override
                    public Observable<RssHandler> call(OpmlEntry opmlEntry) {
                        Logger.d(TAG, "opml entry: " + opmlEntry);
                        return RssReader.parse(opmlEntry.getXmlUrl());
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, "sub");
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.d(TAG, "unsub");
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG, "error", throwable);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RssHandler>() {
                    @Override
                    public void call(RssHandler rssHandler) {
                        if (isViewAttached()) {
                            getView().setData(rssHandler.getEntries());
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
                });
    }
}
