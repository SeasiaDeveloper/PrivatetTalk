package com.privetalk.app.mainflow.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.privetalk.app.R;
import com.privetalk.app.database.datasource.GiftsDatasource;
import com.privetalk.app.database.objects.GiftObject;
import com.privetalk.app.mainflow.fragments.ChatFragments;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;

import java.util.List;

/**
 * Created by zachariashad on 04/03/16.
 */
public class GiftsAdapter extends RecyclerView.Adapter<GiftsAdapter.ViewHolder> {

    private List<GiftObject> giftObjects;
    private Context mContext;

    public GiftsAdapter(Context context) {
        this.mContext = context;
        giftObjects = GiftsDatasource.getInstance(mContext).getGifts();
    }

    /*
    CALLED WHEN A NEW SET OF DATA IS DOWNLOADED FROM SERVER
     */
    public void refreshData() {
        giftObjects.clear();
        giftObjects.addAll(GiftsDatasource.getInstance(mContext).getGifts());
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gitfs, parent, false);

        v.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                Intent intent = new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER);
                intent.putExtra("event_type", PriveTalkConstants.BROADCAST_GIFT_PRESSED);
                intent.putExtra("gift_id", (int) view.getTag(R.id.id_tag));
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (giftObjects.get(position).thumb != null && !giftObjects.get(position).thumb.isEmpty())
            Glide.with(mContext).load(giftObjects.get(position).thumb).into(holder.giftIcon);

        holder.giftCost.setText(String.valueOf(giftObjects.get(position).cost));
        holder.rootView.setTag(R.id.id_tag, giftObjects.get(position).giftId);

    }

    @Override
    public int getItemCount() {
        return giftObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public ImageView giftIcon;
        public PriveTalkTextView giftCost;

        public ViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.giftIcon = (ImageView) rootView.findViewById(R.id.giftIcon);
            this.giftCost = (PriveTalkTextView) rootView.findViewById(R.id.giftCost);
        }
    }
}
