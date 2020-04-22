//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.privetalk.app.utilities;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;


public class RecyclerViewPagerAdapter<VH extends ViewHolder> extends Adapter<VH> {
    private final RecyclerViewPager mViewPager;
    Adapter<VH> mAdapter;

    public RecyclerViewPagerAdapter(RecyclerViewPager viewPager, Adapter<VH> adapter) {
        this.mAdapter = adapter;
        this.mViewPager = viewPager;
        this.setHasStableIds(this.mAdapter.hasStableIds());
    }

    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return this.mAdapter.onCreateViewHolder(parent, viewType);
    }

    public void registerAdapterDataObserver(AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        this.mAdapter.registerAdapterDataObserver(observer);
    }

    public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        this.mAdapter.unregisterAdapterDataObserver(observer);
    }

    public void onBindViewHolder(VH holder, int position) {
        this.mAdapter.onBindViewHolder(holder, position);
        View itemView = holder.itemView;
        LayoutParams lp = itemView.getLayoutParams() == null?new LayoutParams(-1, -1):itemView.getLayoutParams();
        if(this.mViewPager.getLayoutManager().canScrollHorizontally()) {
            lp.width = this.mViewPager.getWidth() - this.mViewPager.getPaddingLeft() - this.mViewPager.getPaddingRight();
        } else {
            lp.height = this.mViewPager.getHeight() - this.mViewPager.getPaddingTop() - this.mViewPager.getPaddingBottom();
        }

        itemView.setLayoutParams(lp);
    }

    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        this.mAdapter.setHasStableIds(hasStableIds);
    }

    public int getItemCount() {
        return this.mAdapter.getItemCount();
    }

    public int getItemViewType(int position) {
        return this.mAdapter.getItemViewType(position);
    }

    public long getItemId(int position) {
        return this.mAdapter.getItemId(position);
    }
}
