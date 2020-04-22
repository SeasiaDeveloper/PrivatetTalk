package com.privetalk.app.mainflow.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.privetalk.app.R;
import com.privetalk.app.database.objects.ProfilePhoto;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.SmoothScrollLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by zachariashad on 23/02/16.
 */
public class FullScreenOtherUsersFragment extends FragmentWithTitle {

    @Override
    protected String getActionBarTitle() {
        return null;
    }

    //views
    private View rootView;
    private ViewPager fullScreenPicturesViewPager;
    private RecyclerView smallPicturePreviewRecyclerView;
    private PercentRelativeLayout.LayoutParams smallPictureRecyclerViewParams;
    private SmoothScrollLinearLayoutManager mSmoothScrollLayoutManager;
    private View removePictureBucket;
    private View shadowView;
    private ImageView tempMovingImageView;
    private ArrayList<ProfilePhoto> photosList;

    //adapters
    private HorizontalRecyclerAdapter smallPicturesPreviewAdapter;
    private FullScreenPicturesPagerAdapter mPagerAdapter;

    //data
    private List<String> profilePictures;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        photosList = (ArrayList<ProfilePhoto>) bundle.getSerializable("other_user_photos");

//        System.out.println("Photos Count: " + photosList.size());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_full_screen_other_people, container, false);

        initViews();

        return rootView;
    }

    private void initViews() {

        fullScreenPicturesViewPager = (ViewPager) rootView.findViewById(R.id.fullScreenPicturePreviewViewPager);
        smallPicturePreviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.smallPicturePreviewRecyclerView);
        smallPictureRecyclerViewParams = (PercentRelativeLayout.LayoutParams) smallPicturePreviewRecyclerView.getLayoutParams();
        removePictureBucket = rootView.findViewById(R.id.removePicture);
        shadowView = rootView.findViewById(R.id.shadowViewFullScreenPicturesFragment);

        fullScreenPicturesViewPager.setAdapter(mPagerAdapter = new FullScreenPicturesPagerAdapter());

        mSmoothScrollLayoutManager = new SmoothScrollLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mSmoothScrollLayoutManager.setMillisecondsPerCebntimeter(500f);

        smallPicturePreviewRecyclerView.setLayoutManager(mSmoothScrollLayoutManager);
        setThumbsRecyclerWidth();
        smallPicturePreviewRecyclerView.setAdapter(smallPicturesPreviewAdapter = new HorizontalRecyclerAdapter());

        fullScreenPicturesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                smallPicturePreviewRecyclerView.smoothScrollToPosition(position);
                smallPicturesPreviewAdapter.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setThumbsRecyclerWidth() {
        int thumbWidth = getResources().getDimensionPixelSize(R.dimen.full_screen_thumbs_width);

        int count = photosList.size();

        if (count == 0)
            smallPictureRecyclerViewParams.width = 0;
        else if (count == 1)
            smallPictureRecyclerViewParams.width = thumbWidth;
        else if (count == 2)
            smallPictureRecyclerViewParams.width = 2 * thumbWidth;
        else if (count == 3)
            smallPictureRecyclerViewParams.width = 3 * thumbWidth;
        else
            smallPictureRecyclerViewParams.width = 4 * thumbWidth;

        smallPicturePreviewRecyclerView.setLayoutParams(smallPictureRecyclerViewParams);
    }


    private class FullScreenPicturesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return photosList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View convertView = LayoutInflater.from(container.getContext()).inflate(R.layout.row_full_screen_pictures_pager, container, false);
            final ImageViewTouch image = (ImageViewTouch) convertView.findViewById(R.id.fullScreenImage);

            image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

            Glide.with(getContext()).load(photosList.get(position).photo).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    image.setImageBitmap(resource);
                }
            });

//            Glide.with(getContext()).load(photosList.get(position).photo).asBitmap().into(image);

            container.addView(convertView);


            return convertView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    private class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.ViewHolder> {

        private int selection = 0;
        private int previousSelection = -1;
        private int borderNormal;
        private int borderBold;


        private HorizontalRecyclerAdapter() {
            borderNormal = getResources().getDimensionPixelSize(R.dimen.border_width_normal);
            borderBold = getResources().getDimensionPixelSize(R.dimen.border_width_bold);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_small_picture_preview_recyclerview, parent, false);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullScreenPicturesViewPager.setCurrentItem((int) v.getTag(R.id.position_tag));
                }
            });

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.imageView.setBorderWidth(borderNormal);
            holder.itemView.setTag(R.id.position_tag, position);

            Glide.with(getContext()).load(photosList.get(position).medium_square_thumb).error(R.drawable.dummy_img).into(holder.imageView);

            if (position == selection)
                holder.imageView.setBorderWidth(borderBold);


//            if (position == selection)
//                holder.imageView.animate().scaleX(1.15f).scaleY(1.15f).setInterpolator(new DecelerateInterpolator()).setDuration(150);
//            if (position == previousSelection)
//                holder.imageView.animate().scaleX(1f).scaleY(1f).setInterpolator(new DecelerateInterpolator()).setDuration(150);
        }

        public void setSelected(int selection) {
            this.previousSelection = this.selection;
            this.selection = selection;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return photosList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView imageView;
            public View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                imageView = (CircleImageView) view.findViewById(R.id.userPictureSmallPreview);
            }
        }

    }


}
