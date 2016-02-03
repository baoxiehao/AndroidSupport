package com.yekong.android.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kennyc.view.MultiStateView;
import com.yekong.android.R;
import com.yekong.android.util.UseCase;
import com.yekong.rss.RssEntry;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.appBarImage)
    ImageView mAppBarImage;

    @Bind(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.multiStateView)
    MultiStateView mMultiStateView;

    @Bind(R.id.markdownView)
    CustomMarkdownView mMarkdownView;

    RssEntry mEntry;
    int mPrevVerticalOffset = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        parseIntent();
        initView();
    }

    private void parseIntent() {
        mEntry = RssEntry.fromJson(getIntent().getStringExtra("entry"));
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Title is gone in the wind after wrapping Toolbar with CollapsingToolbarLayout.
        // We have to set it manually through setTitle.
        mCollapsingToolbarLayout.setTitle(mEntry.getTitle());

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, final int verticalOffset) {
                final boolean appBarCollapsed = mPrevVerticalOffset > verticalOffset && verticalOffset == -550;
                mPrevVerticalOffset = verticalOffset;
                if (appBarCollapsed) {
                    mMarkdownView.setScrollable();
                }
            }
        });

        Glide.with(this).load(R.drawable.app_bar).centerCrop().into(mAppBarImage);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        };
        mMarkdownView.setWebViewClient(webViewClient);
        mMarkdownView.loadUrl(mEntry.getLink());
    }

    @OnClick(R.id.fab)
    void initFab() {
        Snackbar.make(mFab, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Action done", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_browser) {
            UseCase.showWebView(this, mEntry.getLink());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
