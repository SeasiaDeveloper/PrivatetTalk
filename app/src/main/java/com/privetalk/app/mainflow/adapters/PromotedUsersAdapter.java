package com.privetalk.app.mainflow.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.datasource.PromotedUsersDatasource;
import com.privetalk.app.database.objects.PromotedUsersObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;

import java.util.List;

/**
 * Created by zachariashad on 01/03/16.
 */
public class PromotedUsersAdapter extends RecyclerView.Adapter<PromotedUsersAdapter.ViewHolder> {

    public List<PromotedUsersObject> promotedUsersObjects;
    private Context mContext;
    public int imageViewSize = 0;
    private int recyclerViewWidth;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView addMeImage;
        public ImageView addMeCross;
        public View addMe;
        public PriveTalkTextView addMeUserName;
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
            addMeImage = (ImageView) v.findViewById(R.id.addMeImage);
            addMeCross = (ImageView) v.findViewById(R.id.addMeCross);
            addMe = v.findViewById(R.id.addMeText);
            addMeUserName = (PriveTalkTextView) v.findViewById(R.id.addMeUserName);
            addMe.setVisibility(View.GONE);
            addMeCross.setVisibility(View.GONE);
        }
    }

    public PromotedUsersAdapter(Context mContext) {
        this.mContext = mContext;
        promotedUsersObjects = PromotedUsersDatasource.getInstance(mContext).getPromotedUsers();
    }


    public void refreshData() {
        promotedUsersObjects.clear();
        promotedUsersObjects.addAll(PromotedUsersDatasource.getInstance(mContext).getPromotedUsers());
        notifyDataSetChanged();
    }

    public void setImageViewSize(final Callback callback, final RecyclerView recyclerView) {

        recyclerView.post(new Runnable() {
            @Override
            public void run() {

                imageViewSize = recyclerView.getHeight();
                recyclerViewWidth = recyclerView.getWidth();
                callback.done();

            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PromotedUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_promoted_users, parent, false);

        v.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt("other_user_id", promotedUsersObjects.get((int) view.getTag(R.id.position_tag)).id);
                    PriveTalkUtilities.changeFragment(mContext, true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);
                }
            }
        });


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.view.setTag(R.id.position_tag, position % promotedUsersObjects.size());
        holder.addMeUserName.setVisibility(View.VISIBLE);

//        if (position == 0) {
//            holder.addMe.setVisibility(View.VISIBLE);
//            holder.addMeCross.setVisibility(View.VISIBLE);
//            holder.addMeUserName.setVisibility(View.INVISIBLE);
//            if (profilePhoto != null && profilePhoto.square_thumb != null && !profilePhoto.square_thumb.isEmpty())
//                Glide.with(mContext).load(profilePhoto.square_thumb).into(holder.addMeImage);
//        } else {

        holder.addMeUserName.setVisibility(View.VISIBLE);
        holder.addMeUserName.setText(promotedUsersObjects.get(position % promotedUsersObjects.size()).name.split(" ")[0] + ", " + promotedUsersObjects.get(position % promotedUsersObjects.size()).age);

        if (promotedUsersObjects.get(position % promotedUsersObjects.size()).squareThumb != null && !promotedUsersObjects.get(position % promotedUsersObjects.size()).squareThumb.isEmpty())
            Glide.with(mContext).load(promotedUsersObjects.get(position % promotedUsersObjects.size()).squareThumb).into(holder.addMeImage);
        else
            Glide.with(mContext).load(promotedUsersObjects.get(position % promotedUsersObjects.size()).thumb).into(holder.addMeImage);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return promotedUsersObjects.size() > 0
                && recyclerViewWidth != 0
                && recyclerViewWidth < promotedUsersObjects.size() * imageViewSize
                ? Integer.MAX_VALUE : promotedUsersObjects.size();
    }

    public interface Callback {
        void done();
    }
}
