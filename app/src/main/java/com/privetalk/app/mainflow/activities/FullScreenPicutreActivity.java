package com.privetalk.app.mainflow.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.ETagDatasource;
import com.privetalk.app.database.datasource.MessageDatasource;
import com.privetalk.app.database.objects.MessageObject;
import com.privetalk.app.mainflow.fragments.ChatFragments;
import com.privetalk.app.utilities.JsonObjectRequestWithResponse;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by zeniosagapiou on 31/03/16.
 */
public class FullScreenPicutreActivity extends AppCompatActivity {

    private android.os.Handler mHander;
    private long expireTime;
    private MessageObject messageObject;
    private int partnerID;
    private long timeDifference;
    private static final String FIRST_PAGE = "1";
    private JsonObjectRequestWithResponse chatMessagesRequest;
    private BroadcastReceiver chatHandleEventsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String eventType = intent.getStringExtra(PriveTalkConstants.KEY_EVENT_TYPE);

            if (eventType.equals(PriveTalkConstants.BROADCAST_MESSAGE_RECEIVED)
                    && partnerID == intent.getIntExtra("user_id", partnerID) ||
                    partnerID == intent.getIntExtra("user_id2", partnerID)) {


                downloadMessages(FIRST_PAGE, true);
                //downloadmessages


            }
        }
    };
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            expireTime = savedInstanceState.getLong("expireTime");
            downloadMessages(FIRST_PAGE, true);
        } else {
            messageObject = (MessageObject) getIntent().getSerializableExtra(PriveTalkConstants.KEY_MESSAGE);
            partnerID = getIntent().getIntExtra(PriveTalkConstants.PARTNER_ID, -1);
            expireTime = messageObject.expiresOn;
            imageUrl = messageObject.attachmentData;
        }
        timeDifference = PriveTalkUtilities.getGlobalTimeDifference();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(chatHandleEventsReceiver, new IntentFilter(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER));

        overridePendingTransition(R.anim.activity_animation, android.R.anim.fade_out);

        setContentView(R.layout.activity_fullscreen_picture);

        mHander = new android.os.Handler();

        final ImageViewTouch image = (ImageViewTouch) findViewById(R.id.fullScreenImageChat);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        Glide.with(this).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                image.setImageBitmap(resource);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(chatHandleEventsReceiver);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", ChatFragments.FULL_SCREEN_REQUEST);
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHander.removeCallbacks(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        expireCounter();

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1f;
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

    private void expireCounter() {

        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((System.currentTimeMillis() - timeDifference) > expireTime && expireTime != 0)
                    FullScreenPicutreActivity.super.finish();
                else
                    expireCounter();
            }
        }, 1000);

    }

    private void downloadMessages(final String page, final boolean fetchOnlyLatest) {

        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
        headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

        chatMessagesRequest = new JsonObjectRequestWithResponse(Request.Method.GET,
                (page.equals(FIRST_PAGE)) ? (Links.CONVERSATIONS + String.valueOf(partnerID)) : page,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        int statusCode = response.optInt("statusCode");
                        final JSONObject data = response.optJSONObject("data");

                        if (statusCode == 304)
                            return;

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                int userId = CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().userID;

                                if (page.equals(FIRST_PAGE) && !fetchOnlyLatest)
                                    MessageDatasource.getInstance(FullScreenPicutreActivity.this).deleteMessages(userId, partnerID);

                                JSONArray array = data.optJSONArray("results");
                                List<MessageObject> objects = new ArrayList<>();


                                for (int i = 0; i < array.length(); i++)
                                    objects.add(new MessageObject(array.optJSONObject(i), userId));

                                MessageDatasource.getInstance(FullScreenPicutreActivity.this).saveMessages(objects);

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {

                                String nextPage = data.optString("next");
                                if (nextPage != null && !nextPage.isEmpty() && !nextPage.equals("null") && !fetchOnlyLatest)
                                    downloadMessages(nextPage, false);
                                else {
                                    MessageObject messageByMessageId = MessageDatasource.getInstance(PriveTalkApplication.getInstance()).getMessageByMessageId(messageObject.messageID);
                                    if (messageByMessageId != null) {
                                        messageObject = messageByMessageId;
                                        expireTime = messageObject.expiresOn;
                                    }
                                }
                            }

                        }.execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null) {
                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);
                    headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
                }
                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(chatMessagesRequest);
    }

}
