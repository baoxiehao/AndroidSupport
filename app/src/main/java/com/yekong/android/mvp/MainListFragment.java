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
import com.yekong.android.util.UseCase;
import com.yekong.rss.RssEntry;

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
        EfficientAdapter.OnItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    EfficientRecyclerAdapter<RssEntry> mAdapter;

    public static MainListFragment newInstance() {
        return new MainListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable retaining presenter / view state
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(view);
        this.contentView.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new EfficientRecyclerAdapter<>(R.layout.list_item_main,
                MainListViewHolder.class, new ArrayList<RssEntry>());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        loadData(false);
    }

    @Override
    public MainListPresenter createPresenter() {
        return new MainListPresenter();
    }

    @Override
    public void setData(List<RssEntry> data) {
        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        this.presenter.loadMainList(pullToRefresh);
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
    public List<RssEntry> getData() {
        return mAdapter == null ? null : mAdapter.getObjects();
    }

    @Override
    public void showContent() {
        super.showContent();
        this.contentView.setRefreshing(false);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        this.contentView.setRefreshing(pullToRefresh);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        this.contentView.setRefreshing(false);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return "Error";
    }

    @Override
    public void onItemClick(EfficientAdapter adapter, View view, Object object, int position) {
        UseCase.showWebView(getContext(), ((RssEntry) object).getLink());
    }
}
