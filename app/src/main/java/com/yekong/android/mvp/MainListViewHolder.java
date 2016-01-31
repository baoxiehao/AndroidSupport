package com.yekong.android.mvp;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder;
import com.yekong.android.R;
import com.yekong.rss.RssEntry;

/**
 * Created by baoxiehao on 16/1/30.
 */
public class MainListViewHolder extends EfficientViewHolder<RssEntry> {
    /**
     * @param itemView the root view of the view holder. This parameter cannot be null.
     * @throws NullPointerException if the view is null
     */
    public MainListViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void updateView(Context context, RssEntry object) {
        TextView textView = findViewByIdEfficient(R.id.textView);
        TextView subTextView = findViewByIdEfficient(R.id.subTextView);
        TextView dateTextView= findViewByIdEfficient(R.id.dateTextView);
        textView.setText(object.getTitle());
        subTextView.setText(object.getSource());
        dateTextView.setText(object.getPubDate());
    }
}
