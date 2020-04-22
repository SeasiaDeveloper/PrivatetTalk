package com.privetalk.app.utilities.dialogs;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.objects.MutualFriendsObject;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.PriveTalkTextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommonFriendsDialog {

    private RelativeLayout rootView;
    private View dialogView;
    private View dismissDialog;
    private View outerSpace;
    private RelativeLayout.LayoutParams params;
    private Animation in, out;
    private RecyclerView mRecyclerView;
    private CommonFriendsRecyclerAdapter mAdapter;


    public boolean isVisible = false;


    public CommonFriendsDialog(Activity activity, RelativeLayout rootView) {
        this.rootView = rootView;

        //dialog layout params
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        //dialog in and out animations
        in = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in);
        out = AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out);

        dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_common_friends, null);


        //all dialog views
        dismissDialog = dialogView.findViewById(R.id.closeDialog);
        outerSpace = dialogView.findViewById(R.id.commonFriendsDialogOuterSpace);

        mRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialogCommonFriendsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setAdapter(mAdapter = new CommonFriendsRecyclerAdapter(activity));

        outerSpace.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });

        dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                dismiss();
            }
        });


    }

    public CommonFriendsDialog setDataset(List<MutualFriendsObject> mutualFriendsObjects) {
        mAdapter.setList(mutualFriendsObjects);
        return  this;
    }

    public CommonFriendsDialog show() {
        isVisible = true;
        dialogView.setLayoutParams(params);

        rootView.addView(dialogView);

        dialogView.startAnimation(in);
        return this;
    }

    public void dismiss() {
        isVisible = false;
        dialogView.startAnimation(out);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootView.removeView(dialogView);
//                    rootView.removeView(imageView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private class CommonFriendsRecyclerAdapter extends RecyclerView.Adapter<CommonFriendsRecyclerAdapter.ViewHolder> {


        public List<MutualFriendsObject> mutualFriendsList;
        public Context context;

        public CommonFriendsRecyclerAdapter(Context context) {
            this.context = context;
            this.mutualFriendsList = new ArrayList<>();
        }

        public void setList(List<MutualFriendsObject> mutualFriendsList) {
            this.mutualFriendsList.addAll(mutualFriendsList);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View rootView;
            public CircleImageView profilePicture;
            public PriveTalkTextView userName;
            public PriveTalkTextView titleRowTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                rootView = itemView;
                profilePicture = (CircleImageView) rootView.findViewById(R.id.commonFriendsDialogProfilePicture);
                userName = (PriveTalkTextView) rootView.findViewById(R.id.commonFriendsDialogUserName);
                titleRowTitle = (PriveTalkTextView) rootView.findViewById(R.id.commonFriendsDialogRowTitle);
            }
        }

        @Override
        public CommonFriendsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dialog_common_friends, null);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CommonFriendsRecyclerAdapter.ViewHolder holder, int position) {

            holder.titleRowTitle.setVisibility(View.INVISIBLE);
            holder.userName.setText(mutualFriendsList.get(position).name);
            if (mutualFriendsList.get(position).fbID != null && !mutualFriendsList.get(position).fbID.isEmpty())
                Glide.with(context).load("http://graph.facebook.com/" + mutualFriendsList.get(position).fbID + "/picture").into(holder.profilePicture);

        }

        @Override
        public int getItemCount() {
            return mutualFriendsList.size();
        }
    }

}