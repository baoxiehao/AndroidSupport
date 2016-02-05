package com.yekong.android.util;

import android.content.Context;
import android.content.Intent;

import com.thefinestartist.finestwebview.FinestWebView;
import com.yekong.android.R;
import com.yekong.android.ui.DetailActivity;
import com.yekong.rss.RssEntry;

/**
 * Created by baoxiehao on 16/2/3.
 */
public class UseCase {
    public static void showDetailActivity(Context context, RssEntry rssEntry) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("entry", rssEntry.toString());
        context.startActivity(intent);
    }

    public static void showWebView(Context context, final String url) {
        new FinestWebView.Builder(context)
                .iconDefaultColorRes(R.color.icon)
                .progressBarColorRes(R.color.icon)
                .stringResRefresh(R.string.web_refresh)
                .stringResShareVia(R.string.web_share)
                .stringResCopyLink(R.string.web_copy_link)
                .stringResOpenWith(R.string.web_open_with)
                .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit,
                        R.anim.activity_close_enter, R.anim.activity_close_exit)
                .show(url);
    }
}
