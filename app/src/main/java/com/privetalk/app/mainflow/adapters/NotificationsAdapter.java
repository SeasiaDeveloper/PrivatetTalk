package com.privetalk.app.mainflow.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.NotificationsDatasource;
import com.privetalk.app.database.objects.ConversationObject;
import com.privetalk.app.database.objects.NotificationObject;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.DrawerUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.JsonObjectRequestWithResponse;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zachariashad on 15/03/16.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> implements Application.ActivityLifecycleCallbacks {

    private Context context;
    private List<NotificationObject> objects;
    private SimpleDateFormat dateFormat;
    private JsonObjectRequestWithResponse getmessageRequest;
    private boolean uiIsBlocked;
    private boolean royalUser;
    private AlertDialog dialog;
//    private String message;

    public NotificationsAdapter(Context context) {
        this.context = context;
        this.objects = NotificationsDatasource.getInstance(context).getAllNotifications();
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        if (CurrentUserDatasource.getInstance(context).getCurrentUserInfo() != null)
            this.royalUser = CurrentUserDatasource.getInstance(context).getCurrentUserInfo().royal_user;
    }

    public void markAllNotificationsAsRed() {

        for (NotificationObject notificationObject : objects)
            notificationObject.notificationRead = true;

        NotificationsDatasource.getInstance(context).saveNotificationsList(objects);
    }

    public void refreshDataset() {
        this.objects.clear();
        this.objects.addAll(NotificationsDatasource.getInstance(context).getAllNotifications());
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notifications, parent, false);

        v.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                if (uiIsBlocked) {
                    return;
                }


                NotificationObject object = objects.get((int) view.getTag(R.id.position_tag));

                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_DRAWER_FRAGMENT);
                switch (object.type) {
                    case NotificationObject.TYPE_MSG:
                        object.showLoading = true;
                        checkIfMessageIsDeleted((int) view.getTag(R.id.position_tag));
                        notifyDataSetChanged();
                        break;
                    case NotificationObject.TYPE_PROFILE_VISITOR:
                        intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, DrawerUtilities.DrawerRow.PROFILE_VISITORS.ordinal());
                        LocalBroadcastManager.getInstance(parent.getContext()).sendBroadcast(intent);
                        break;
                    case NotificationObject.TYPE_FLAME_IGNITED:
                        intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, DrawerUtilities.DrawerRow.FLAMES_IGNITED.ordinal());
                        LocalBroadcastManager.getInstance(parent.getContext()).sendBroadcast(intent);
                        break;
                    /*case NotificationObject.TYPE_HOTMATCH:
                        intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, DrawerUtilities.DrawerRow.HOT_MATCHES.ordinal());
                        LocalBroadcastManager.getInstance(parent.getContext()).sendBroadcast(intent);
                        break;*/
                    case NotificationObject.TYPE_FAVORITE:
                        intent.putExtra(DrawerUtilities.FRAGMENTT_CHANGE, DrawerUtilities.DrawerRow.FAVORITES.ordinal());
                        LocalBroadcastManager.getInstance(parent.getContext()).sendBroadcast(intent);
                        break;
                }

            }
        });

        return new ViewHolder(v);
    }

    private void checkIfMessageIsDeleted(final int position) {

        if (getmessageRequest != null)
            getmessageRequest.cancel();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(PriveTalkConstants.KEY_PARTNER_ID, objects.get(position).senderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        uiIsBlocked = true;
//        dialog = new MaterialProgressDialogBuilder(context).create();
//        dialog.show();

        getmessageRequest = new JsonObjectRequestWithResponse(Request.Method.GET, Links.CONVERSATIONS + objects.get(position).senderId + "/", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        dialog.dismiss();
                        objects.get(position).showLoading = false;
                        notifyDataSetChanged();


                        Bundle bundle = new Bundle();

                        ConversationObject conversationObject = new ConversationObject();
                        conversationObject.partnerID = objects.get(position).senderId;
                        conversationObject.description = objects.get(position).message;
                        conversationObject.partnerName = objects.get(position).senderName;
                        conversationObject.partnerAvatarImg = objects.get(position).thumbPhoto;
                        bundle.putSerializable("partnerObject", conversationObject);

                        Bundle globalBundle = new Bundle();
                        globalBundle.putBundle("bundle", bundle);

                        PriveTalkUtilities.changeFragment(context, true, PriveTalkConstants.CHAT_FRAGMENT, globalBundle);

                        uiIsBlocked = false;
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
                objects.get(position).showLoading = false;
                notifyDataSetChanged();
                uiIsBlocked = false;

                if (error.networkResponse != null) {
                    try {
                        if (error.networkResponse.statusCode == 404) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.conversation_deleted));
                            builder.setTitle(context.getString(R.string.could_not_find_conversation));
                            builder.setNeutralButton(context.getString(R.string.okay), null);
                            builder.create().show();

                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                Toast.makeText(context, jsonObject.optString("detail"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getmessageRequest);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.message.setText("{b}" + objects.get(position).message.replaceFirst(" ", "{/b} "));
        holder.date.setTime(objects.get(position).createdOn);
        holder.dateView.setText(dateFormat.format(holder.date));
        holder.profileLogoPicture.setVisibility(View.GONE);
        holder.profilePicture.setImageResource(android.R.color.white);


        switch (objects.get(position).type) {

            case NotificationObject.TYPE_MSG:
                Glide.with(context).load(objects.get(position).thumbPhoto).error(R.drawable.dummy_img).fitCenter().into(holder.profilePicture);
                break;

            case NotificationObject.TYPE_HOTMATCH:
                Glide.with(context).load(objects.get(position).thumbPhoto)
                        .listener(new RequestListener<Serializable, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, Serializable model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.profileLogoPicture.setVisibility(View.VISIBLE);
                                Glide.with(context).load(R.drawable.match_icon).fitCenter().into(holder.profileLogoPicture);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Serializable model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).fitCenter().into(holder.profilePicture);
                break;

            case NotificationObject.TYPE_PROFILE_VISITOR:
                holder.profileLogoPicture.setVisibility(royalUser ? View.INVISIBLE : View.VISIBLE);
                Glide.with(context).load(royalUser ? objects.get(position).thumbPhoto : R.drawable.eye_icon)
                        .listener(new RequestListener<Serializable, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, Serializable model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.profileLogoPicture.setVisibility(View.VISIBLE);
                                Glide.with(context).load(R.drawable.eye_icon).fitCenter().into(holder.profileLogoPicture);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Serializable model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).fitCenter().into(royalUser ? holder.profilePicture : holder.profileLogoPicture);
                break;

            case NotificationObject.TYPE_FAVORITE:
                holder.profileLogoPicture.setVisibility(royalUser ? View.INVISIBLE : View.VISIBLE);
                Glide.with(context).load(royalUser ? objects.get(position).thumbPhoto : R.drawable.star_icon)
                        .listener(new RequestListener<Serializable, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, Serializable model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Glide.with(context).load(R.drawable.star_icon).fitCenter().into(holder.profileLogoPicture);
                                holder.profileLogoPicture.setVisibility(View.VISIBLE);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Serializable model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).fitCenter().into(royalUser ? holder.profilePicture : holder.profileLogoPicture);
                break;

            case NotificationObject.TYPE_A_MSG:
                Glide.with(context).load(R.drawable.privetal_logo).fitCenter().into(holder.profileLogoPicture);
                holder.profileLogoPicture.setVisibility(View.VISIBLE);
                break;

            case NotificationObject.TYPE_ALERT:
                Glide.with(context).load(R.drawable.privetal_logo).fitCenter().into(holder.profileLogoPicture);
                holder.profileLogoPicture.setVisibility(View.VISIBLE);
                break;

            case NotificationObject.TYPE_S_MSG:
                Glide.with(context).load(R.drawable.privetal_logo).fitCenter().into(holder.profileLogoPicture);
                holder.profileLogoPicture.setVisibility(View.VISIBLE);
                break;

            case NotificationObject.TYPE_FLAME_IGNITED:
                holder.profileLogoPicture.setVisibility(royalUser ? View.INVISIBLE : View.VISIBLE);

                Glide.with(context).load(royalUser ? objects.get(position).thumbPhoto : R.drawable.match_icon)
                        .listener(new RequestListener<Serializable, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, Serializable model, Target<GlideDrawable> target, boolean isFirstResource) {

                                Glide.with(context).load(R.drawable.match_icon).fitCenter().into(holder.profileLogoPicture);
//                                Glide.with(context).load(R.drawable.match_icon).into(holder.profileLogoPicture);
//
                                holder.profileLogoPicture.setVisibility(View.VISIBLE);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Serializable model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).fitCenter().into(royalUser ? holder.profilePicture : holder.profileLogoPicture);

                break;

        }

        holder.itemView.setTag(R.id.id_tag, objects.get(position).senderId);
        holder.itemView.setTag(R.id.position_tag, position);
        holder.progressBar.setVisibility(objects.get(position).showLoading ? View.VISIBLE : View.GONE);
    }


    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }


    @Override
    public void onActivityStarted(Activity activity) {

    }


    @Override
    public void onActivityResumed(Activity activity) {

    }


    @Override
    public void onActivityPaused(Activity activity) {
        if (getmessageRequest != null)
            getmessageRequest.cancel();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profilePicture;
        private PriveTalkTextView message;
        private PriveTalkTextView dateView;
        private ImageView profileLogoPicture;
        private View progressBar;
        //        public ImageView notificationIcon;
        public Date date = new Date(System.currentTimeMillis());

        public ViewHolder(View itemView) {
            super(itemView);
            this.profilePicture = (CircleImageView) itemView.findViewById(R.id.senderProfilePicture);
            this.profileLogoPicture = (ImageView) itemView.findViewById(R.id.priveTalkLogoView);
            this.message = (PriveTalkTextView) itemView.findViewById(R.id.notificationMessage);
            this.dateView = (PriveTalkTextView) itemView.findViewById(R.id.notificationDate);
            this.progressBar = itemView.findViewById(R.id.messageProgressBar);
//            this.notificationIcon = (ImageView) itemView.findViewById(R.id.notificationIcon);
        }
    }
}
