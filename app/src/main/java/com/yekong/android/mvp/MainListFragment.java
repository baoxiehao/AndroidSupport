package com.yekong.android.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.skocken.efficientadapter.lib.adapter.EfficientAdapter;
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter;
import com.yekong.android.R;
import com.yekong.android.rss.RssConfig;
import com.yekong.android.rss.RssEntry;
import com.yekong.android.util.Logger;
import com.yekong.android.util.UseCase;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by baoxiehao on 16/1/29.
 */
public class MainListFragment extends MvpLceViewStateFragment<SwipeRefreshLayout,
        List<RssEntry>, MainListView, MainListPresenter>
        implements MainListView, SwipeRefreshLayout.OnRefreshListener,
        EfficientAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MainListFragment";

    private static final String ARG_CATEGORY = "ARG_CATEGORY";

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.contentView)
    SwipeRefreshLayout mSwipeRefreshLayout;

    EfficientRecyclerAdapter<RssEntry> mAdapter;

    RssConfig.Category mCategory;

    public static MainListFragment newInstance(RssConfig.Category category) {
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, RssConfig.Category.toJson(category));
        MainListFragment fragment = new MainListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable retaining presenter / view state
        setRetainInstance(true);
        Bundle args = getArguments();
        mCategory = RssConfig.Category.fromJson(args.getString(ARG_CATEGORY));
    }

    @Override
    public void onDestroy() {
        this.presenter.saveData(getContext());
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d(TAG, String.format("onViewCreated: tag=%s", mCategory.name.toUpperCase()));
        ButterKnife.bind(this, view);
        this.contentView.setOnRefreshListener(this);
        this.errorView.setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryDark);
        mAdapter = new EfficientRecyclerAdapter<>(R.layout.list_item_main,
                MainListViewHolder.class, new ArrayList<RssEntry>());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        showLoading(false);
        loadData(false);
    }

    @Override
    public MainListPresenter createPresenter() {
        return new MainListPresenter(mCategory);
    }

    @Override
    public void setData(List<RssEntry> data) {
        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public List<RssEntry> getData() {
        return mAdapter == null ? new ArrayList<RssEntry>() : mAdapter.getObjects();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        this.presenter.loadMainList(getContext(), pullToRefresh);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public LceViewState<List<RssEntry>, MainListView> createViewState() {
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        Logger.d(TAG, String.format("showContent: tag=%s", mCategory.name.toUpperCase()));
        this.contentView.setRefreshing(false);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        Logger.d(TAG, String.format("showLoading: tag=%s", mCategory.name.toUpperCase()));
        this.contentView.setRefreshing(pullToRefresh);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        Logger.d(TAG, String.format("showError: tag=%s", mCategory.name.toUpperCase()));
        this.contentView.setRefreshing(false);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getString(R.string.mvp_error_tip);
    }

    @Override
    public void onItemClick(EfficientAdapter adapter, View view, Object object, int position) {
//        UseCase.showDetailActivity(getContext(), (RssEntry) object);
        UseCase.showWebView(getContext(), ((RssEntry) object).getLink());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.errorView:
                showLoading(false);
                loadData(true);
                break;
        }
    }
}
