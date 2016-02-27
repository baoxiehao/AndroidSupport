package com.yekong.android.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yekong.android.R;
import com.yekong.android.mvp.MainListFragment;
import com.yekong.android.rss.RssConfig;
import com.yekong.android.rss.RssFactory;
import com.yekong.android.util.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.navView)
    NavigationView mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);

        initViewPager();
    }

    private void initViewPager() {
        final FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        RssFactory.getInstance(this).categoryObservable()
                .toList()
                .filter(new Func1<List<RssConfig.Category>, Boolean>() {
                    @Override
                    public Boolean call(List<RssConfig.Category> categories) {
                        return !categories.isEmpty();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<RssConfig.Category>>() {
                    @Override
                    public void call(List<RssConfig.Category> categories) {
                        for (RssConfig.Category category : categories) {
                            adapter.addFragment(MainListFragment.newInstance(category), category.name);
                        }
                        Logger.d(TAG, "initViewPager next: size=" + categories.size());
                        if (mViewPager != null) {
                            mViewPager.setAdapter(adapter);
                            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                            tabLayout.setupWithViewPager(mViewPager);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(TAG, "initViewPager error", throwable);
                    }
                });
    }

    @OnClick(R.id.fab)
    void initFab() {
        Snackbar.make(mFab, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Action done", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rss) {
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_setting) {
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
