package com.privetalk.app.utilities.dialogs;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.PriveTalkTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zachariashad on 05/04/16.
 */
public class CommonInterestsPopUp extends PopupWindow {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private View inflatedView;
    private HashMap<String, ArrayList<InterestObject>> commonInterests;


    public CommonInterestsPopUp(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        this.inflatedView = contentView;
    }

    public CommonInterestsPopUp initiliaze(Context context, final View rootView, HashMap<String, ArrayList<InterestObject>> commonInterests) {
        this.mContext = context;
        this.commonInterests = commonInterests;

        CommonInterestsPopUp.this.setTouchable(true);
        CommonInterestsPopUp.this.setBackgroundDrawable(null);
        setBackgroundDrawable(new BitmapDrawable());
        CommonInterestsPopUp.this.setOutsideTouchable(true);
        CommonInterestsPopUp.this.setAnimationStyle(R.style.PopupWindowAnimationStyle);
        View commonFriendsIcon = rootView.findViewById(R.id.commonInterestsIcon);

        mRecyclerView = (RecyclerView) inflatedView.findViewById(R.id.dialogCommonInterestsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(new CommonInterestsRecyclerAdapter());

        inflatedView.findViewById(R.id.closeDialog).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                CommonInterestsPopUp.this.dismiss();
            }
        });

        CommonInterestsPopUp.this.showAtLocation(rootView, Gravity.LEFT | Gravity.TOP,
                (commonFriendsIcon.getRight() - mContext.getResources().getDimensionPixelSize(R.dimen.popup_common_friends_width)) + commonFriendsIcon.getWidth(),
                (int) commonFriendsIcon.getY() - commonFriendsIcon.getHeight());

        return this;
    }


    private class CommonInterestsRecyclerAdapter extends RecyclerView.Adapter<CommonInterestsRecyclerAdapter.ViewHolder> {

        private String notSetYetString;
        private static final int ACTIVITIES = 0;
        private static final int MUSIC = 1;
        private static final int MOVIES = 2;
        private static final int LITERATURE = 3;
        private static final int PLACES = 4;
        private static final int COUNT = 5;

        public CommonInterestsRecyclerAdapter() {
            notSetYetString = mContext.getString(R.string.not_yet_set);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View rootView;
            public ImageView interestIcon;
            public PriveTalkTextView interestName;
            public String commonInterestString;

            public ViewHolder(View itemView) {
                super(itemView);
                this.rootView = itemView;
                interestIcon = (ImageView) rootView.findViewById(R.id.commonInterestsDialogActivityIcon);
                interestName = (PriveTalkTextView) rootView.findViewById(R.id.commonInterestDialogActivityName);
            }
        }

        @Override
        public CommonInterestsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_common_interests_dialog, null);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CommonInterestsRecyclerAdapter.ViewHolder holder, int position) {
            if (commonInterests != null)
                switch (position) {
                    case ACTIVITIES:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("a"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(mContext).load(R.drawable.profile_interests_activities).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MUSIC:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("m"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(mContext).load(R.drawable.profile_interests_music).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MOVIES:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("mo"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(mContext).load(R.drawable.profile_interests_movies).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case LITERATURE:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("l"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(mContext).load(R.drawable.profile_interests_literature).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;
                    case PLACES:
                        holder.commonInterestString = InterestObject.getStringFromList(commonInterests.get("p"));
                        if (holder.commonInterestString.equals(notSetYetString))
                        {
                            holder.interestName.setVisibility(View.GONE);
                            holder.interestIcon.setVisibility(View.GONE);
                        }
                        else {
                            Glide.with(mContext).load(R.drawable.profile_interests_places).into(holder.interestIcon);
                            holder.interestName.setText(holder.commonInterestString);
                            holder.interestName.setVisibility(View.VISIBLE);
                            holder.interestIcon.setVisibility(View.VISIBLE);
                        }
                        break;

                }

        }

        @Override
        public int getItemCount() {
            return COUNT;
        }
    }


}
