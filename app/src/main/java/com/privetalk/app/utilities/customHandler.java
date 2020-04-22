package com.privetalk.app.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.privetalk.app.mainflow.activities.MainActivity;
import com.pushbots.push.PBNotificationIntent;
import com.pushbots.push.Pushbots;
import com.pushbots.push.utils.PBConstants;

import org.json.JSONObject;

import java.util.HashMap;

public class customHandler extends BroadcastReceiver {
    private static final String TAG = "customHandler";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        // Handle Push Message when opened
        if (action.equals(PBConstants.EVENT_MSG_OPEN)) {
            //Check for Pushbots Instance
            Pushbots pushInstance = Pushbots.sharedInstance();
            if (!pushInstance.isInitialized()) {
                com.pushbots.push.utils.Log.d("Initializing Pushbots.");
                Pushbots.sharedInstance().init(context.getApplicationContext());
            }

            //Clear Notification array
            if (PBNotificationIntent.notificationsArray != null) {
                PBNotificationIntent.notificationsArray = null;
            }

            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_OPEN);

            //Report Opened Push Notification to Pushbots
            if (Pushbots.sharedInstance().isAnalyticsEnabled()) {
                Pushbots.sharedInstance().reportPushOpened((String) PushdataOpen.get("PUSHANALYTICS"));
            }

            Intent mainActivityIntent = new Intent(context, MainActivity.class);


//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

//                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {

                context.getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE)
                        .edit().putString(PriveTalkConstants.KEY_PUSH_DATA, (new JSONObject(PushdataOpen)).toString()).apply();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            context.startActivity(mainActivityIntent);


            // Handle Push Message when received
        } else if (action.equals(PBConstants.EVENT_MSG_RECEIVE)) {
            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_RECEIVE);
            Log.w(TAG, "User Received notification with Message: " + PushdataOpen.get("message"));
        }
    }
}