package com.privetalk.app.mainflow.adapters;

import android.content.Context;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.viewpager.widget.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.objects.CurrentUserPhotoObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PagerCarouselAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private SparseArray<View> viewCollector;
    public int viewPagerHeight;
    private List<CurrentUserPhotoObject> photosList;

    public PagerCarouselAdapter(Context mContext, LayoutInflater inflater, int viewPagerHeight) {
        this.mContext = mContext;
        this.inflater = inflater;
        this.viewPagerHeight = viewPagerHeight;
        this.photosList = CurrentUserPhotosDatasource.getInstance(mContext).getCurrentUserPhotos();
        viewCollector = new SparseArray<>();


    }

    public int getPictureId(int position) {

        return photosList != null && photosList.size() > position ? photosList.get(position).id : -1;
    }

    //return position none so the view pager will remove all views and reload them all
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    //called when a new set of pictures
    @Override
    public void notifyDataSetChanged() {
        this.photosList.clear();
        this.photosList.addAll(CurrentUserPhotosDatasource.getInstance(mContext).getCurrentUserPhotos());
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photosList.size();
    }

    @Override
    public boolean isViewFromObject(View viewIn, Object obj) {

        return viewIn == obj;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        viewCollector.remove(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View rowLayout = inflater.inflate(R.layout.row_boost_popularity, container, false);

        RelativeLayout parentLayout = (RelativeLayout) rowLayout.findViewById(R.id.viewPagerCenter);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(viewPagerHeight, viewPagerHeight);
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        parentLayout.setLayoutParams(params1);

        CircleImageView imageView = (CircleImageView) rowLayout.findViewById(R.id.boostProfilePicture);

        PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(viewPagerHeight, viewPagerHeight);
        params.addRule(PercentRelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(params);

        viewCollector.put(position, rowLayout);

        Glide.with(mContext).load(photosList.get(position).large_square_thumb).error(R.drawable.dummy_img).into(imageView);

        imageView.setTag(R.id.position_tag, position);

        container.addView(rowLayout);

        return rowLayout;
    }


}
