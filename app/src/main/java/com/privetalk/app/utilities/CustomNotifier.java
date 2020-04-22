package com.privetalk.app.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.privetalk.app.R;
import com.privetalk.app.mainflow.fragments.ChatFragments;
import com.pushbots.push.GCMIntentService;
import com.pushbots.push.Pushbots;
import com.pushbots.push.utils.Log;
import com.pushbots.push.utils.PBConstants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zachariashad on 14/04/16.
 */
public class CustomNotifier extends GCMIntentService {

    public CustomNotifier(){

    }


    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i("Received a Notification.");

        // Notification Data to be sent to the intent
        HashMap<String, String> localHashMap = new HashMap<String, String>();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                localHashMap.put(key, bundle.getString(key));
            }
        }


        Class<?> localClass = Pushbots.sharedInstance().CustomHandler();
        if (localClass != null) {
            Intent localIntent = new Intent(PBConstants.EVENT_MSG_RECEIVE);
            localIntent.putExtra(PBConstants.EVENT_MSG_RECEIVE, localHashMap);
            localIntent.setClass(context, customHandler.class);
            sendBroadcast(localIntent);
        } else {
        }

        // Notify the user
        if (localHashMap.get("refresh_chat") != null && localHashMap.get("refresh_chat").equals("true")) {
            Intent broadcastIntent = new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER);
            broadcastIntent.putExtra("event_type", PriveTalkConstants.BROADCAST_MESSAGE_RECEIVED);

            broadcastIntent.putExtra("user_id",
                    (localHashMap.get("sender") != null && !localHashMap.get("sender").equals("null")) ? Integer.parseInt(localHashMap.get("sender")) : 0);

            broadcastIntent.putExtra("user_id2",
                    (localHashMap.get("reader") != null && !localHashMap.get("reader").equals("null")) ? Integer.parseInt(localHashMap.get("reader")) : 0);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
        } else {

            if (localHashMap.get("is_chat") != null && localHashMap.get("is_chat").equals("true")) {

                Intent broadcastIntent = new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER);
                broadcastIntent.putExtra("event_type", PriveTalkConstants.BROADCAST_MESSAGE_RECEIVED);

                broadcastIntent.putExtra("user_id",
                        (localHashMap.get("sender") != null && !localHashMap.get("sender").equals("null")) ? Integer.parseInt(localHashMap.get("sender")) : 0);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);

                PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.CONVERSATIONS, PriveTalkConstants.BROADCAST_CONVERSATIONS_DOWNLOADED, null, new JSONObject());

                if (localHashMap.get("sender") != null && !localHashMap.get("sender").equals("null")) {
                    String currentChatGuyId = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE).getString(PriveTalkConstants.CURRENT_CHAT_GUY, "");

                    if (localHashMap.get("sender").equals(currentChatGuyId)) {
                        PriveTalkUtilities.getNotifications();
                        PriveTalkUtilities.badgesRequest(false, null);
                        return;
                    }

                }
            }

            Bundle extras = intent.getExtras();
            extras.putString("largeIcon", "drawable://" + R.mipmap.ic_launcher);
            extras.putString("customIcon", "white_notification_icon");
            intent.putExtras(extras);


            Pushbots.sharedInstance().NotificationBuilder().generateNotification(context, intent, new Handler());
            PriveTalkUtilities.getNotifications();
            PriveTalkUtilities.badgesRequest(false, null);
        }
    }


    @Override
    protected void onDeletedMessages(Context context, int total) {
        super.onDeletedMessages(context, total);
    }

    @Override
    public void onError(Context context, String errorId) {
        super.onError(context, errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }
}
