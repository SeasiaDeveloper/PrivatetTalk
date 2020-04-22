package com.privetalk.app.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.privetalk.app.R;

/**
 * Created by zachariashad on 18/05/16.
 */
public class SearchFilterContainer extends RelativeLayout {

    //    private String rowText;
    private PriveTalkTextView filterText;
    private View clearFilter;
    private int resId;
    public int selectedFilter = -1;
//    private MySwitch emailSwitch;
//    private MySwitch phoneSwitch;
//    private PriveTalkTextView textView;
//    private ProgressBar progressBar;

    public SearchFilterContainer(Context context) {
        super(context);
        initView(LayoutInflater.from(context));
    }

    public SearchFilterContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(LayoutInflater.from(context));
    }

    public SearchFilterContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(LayoutInflater.from(context));
    }


    private void initView(LayoutInflater mInflater) {
        View convertView = mInflater.inflate(R.layout.layout_search_filter_container, this, true);
        filterText = (PriveTalkTextView) convertView.findViewById(R.id.filterTitle);
        clearFilter = convertView.findViewById(R.id.clearFilter);


    }


    public void setSelectedFilter(int selectedFilter) {
        this.selectedFilter = selectedFilter;
    }


    public void setTitle(int resId) {
        this.resId = resId;
        this.filterText.setText(getContext().getString(resId));
    }

    public void setFilterText(String filterText) {
        this.filterText.setText(getContext().getString(this.resId) + ": {r}" + filterText + "{/r}");
        this.clearFilter.setVisibility(VISIBLE);
        this.clearFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(resId);
                v.setVisibility(GONE);
                setSelectedFilter(-1);
            }
        });

    }
}
