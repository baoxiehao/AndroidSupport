package com.yekong.android.mvp;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.yekong.rss.RssHandler;
import com.yekong.rss.RssReader;
import com.yekong.rss.RssTest;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by baoxiehao on 16/1/29.
 */
public class MainListPresenter extends MvpBasePresenter<MainListView> {
    public MainListPresenter() {
    }

    @Override
    public void attachView(MainListView view) {
        super.attachView(view);
        Log.d("bao", "attach");
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        Log.d("bao", "detach");
    }

    public void loadMainList(final String query, final boolean pullToRefresh) {
        RssReader.parse(RssTest.getRandomRss())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.d("bao", "sub");
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.d("bao", "unsub");
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("bao", "error", throwable);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.d("bao", "done");
                    }
                })
                .subscribeOn(Schedulers.io())
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
