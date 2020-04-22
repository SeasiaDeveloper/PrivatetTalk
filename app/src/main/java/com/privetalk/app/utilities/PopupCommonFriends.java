package com.privetalk.app.utilities;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.objects.MutualFriendsObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zachariashad on 05/04/16.
 */
public class PopupCommonFriends extends PopupWindow {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private View inflatedView;


    public PopupCommonFriends(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        this.inflatedView = contentView;
    }

    public PopupCommonFriends initiliaze(Context context, final View rootView, final List<MutualFriendsObject> mutualFriendsObjects){
        this.mContext = context;
        PopupCommonFriends.this.setTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        PopupCommonFriends.this.setOutsideTouchable(true);
        PopupCommonFriends.this.setAnimationStyle(R.style.PopupWindowAnimationStyle);
        View commonFriendsIcon = rootView.findViewById(R.id.commonFriendsIcon);


        mRecyclerView = (RecyclerView) inflatedView.findViewById(R.id.dialogCommonFriendsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(new CommonFriendsRecyclerAdapter(mContext, mutualFriendsObjects));


        inflatedView.findViewById(R.id.closeDialog).setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PopupCommonFriends.this.dismiss();
            }
        });

        PopupCommonFriends.this.showAtLocation(rootView, Gravity.LEFT | Gravity.TOP,
                (commonFriendsIcon.getRight() - mContext.getResources().getDimensionPixelSize(R.dimen.popup_common_friends_width)) + commonFriendsIcon.getWidth(),
                (int) commonFriendsIcon.getY() - commonFriendsIcon.getHeight());

        return this;
    }



    private class CommonFriendsRecyclerAdapter extends RecyclerView.Adapter<CommonFriendsRecyclerAdapter.ViewHolder> {


        public List<MutualFriendsObject> mutualFriendsList;
        public Context context;

        public CommonFriendsRecyclerAdapter(Context context, List<MutualFriendsObject> mutualFriendsList) {
            this.context = context;
            this.mutualFriendsList = mutualFriendsList;
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
