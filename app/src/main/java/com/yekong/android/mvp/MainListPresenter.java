package com.yekong.android.mvp;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.yekong.android.cache.Provider;
import com.yekong.android.rss.RssEntry;
import com.yekong.android.util.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
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
        Provider.getInstance(context)
                .allEntries()
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
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG, "error: " + pullToRefresh, throwable);
                    }
                })
                .doOnNext(new Action1<List<RssEntry>>() {
                    @Override
                    public void call(List<RssEntry> rssEntries) {
                        Logger.d(TAG, "load rss entries: " + rssEntries.size() + ", " + rssEntries.hashCode());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<RssEntry>>() {
                    @Override
                    public void call(List<RssEntry> rssEntries) {
                        if (isViewAttached()) {
                            getView().addData(rssEntries);
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
