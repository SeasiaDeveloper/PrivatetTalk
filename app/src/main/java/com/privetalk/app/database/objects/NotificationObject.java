package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zachariashad on 15/03/16.
 */
public class NotificationObject {

    public static final String TYPE_MSG = "msg";
    public static final String TYPE_A_MSG = "amsg";
    public static final String TYPE_S_MSG = "smsg";
    public static final String TYPE_ALERT = "alert";
    public static final String TYPE_PROFILE_VISITOR = "prvis";
    public static final String TYPE_FLAME_IGNITED = "flame";
    public static final String TYPE_HOTMATCH = "hotmatch";
    public static final String TYPE_FAVORITE = "favourited";


    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMM yyyy - HH:mm", Locale.US);

    public int id;
    public int senderId;
    public String senderName = "";
    public String thumbPhoto = "";
    public String message = "";
    public String type = "";
    public long createdOn;
    public int receiver;
    public boolean notificationRead;
    public boolean showLoading = false;


    public NotificationObject(JSONObject obj) {
        this.id = obj.optInt("id");
        this.message = obj.optString("message");
        this.type = obj.optString("type");
        this.receiver = obj.optInt("receiver");

        try {
            this.createdOn = dateFormat.parse(obj.optString("created_on")).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            JSONObject senderObject = obj.getJSONObject("sender");
            this.senderId = senderObject.optInt("id");
            this.senderName = senderObject.optString("name");

            JSONObject mainProfilePhoto = senderObject.getJSONObject("main_profile_photo");
            this.thumbPhoto = mainProfilePhoto.optString("square_thumb");

        } catch (JSONException e) {
//            e.printStackTrace();
        }

    }

    public NotificationObject(Cursor cursor) {

        this.id = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.ID));
        this.senderId = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.SENDER_ID));
        this.senderName = cursor.getString(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.SENDER_NAME));
        this.thumbPhoto = cursor.getString(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.THUMB_PHOTO));
        this.message = cursor.getString(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.MESSAGE));
        this.type = cursor.getString(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.TYPE));
        this.createdOn = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.CREATED_ON));
        this.receiver = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.RECEIVER));
        this.notificationRead = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.NOTIFICATION_READ)) == 1;

    }


    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.NotificationsTable.ID, this.id);
        contentValues.put(PriveTalkTables.NotificationsTable.SENDER_ID, this.senderId);
        contentValues.put(PriveTalkTables.NotificationsTable.SENDER_NAME, this.senderName);
        contentValues.put(PriveTalkTables.NotificationsTable.THUMB_PHOTO, this.thumbPhoto);
        contentValues.put(PriveTalkTables.NotificationsTable.MESSAGE, this.message);
        contentValues.put(PriveTalkTables.NotificationsTable.TYPE, this.type);
        contentValues.put(PriveTalkTables.NotificationsTable.CREATED_ON, this.createdOn);
        contentValues.put(PriveTalkTables.NotificationsTable.RECEIVER, this.receiver);
        contentValues.put(PriveTalkTables.NotificationsTable.NOTIFICATION_READ, this.notificationRead ? 1 : 0);

        return contentValues;
    }

    public ContentValues getContentValuesForUpdate(int norificationRed) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.NotificationsTable.ID, this.id);
        contentValues.put(PriveTalkTables.NotificationsTable.SENDER_ID, this.senderId);
        contentValues.put(PriveTalkTables.NotificationsTable.SENDER_NAME, this.senderName);
        contentValues.put(PriveTalkTables.NotificationsTable.THUMB_PHOTO, this.thumbPhoto);
        contentValues.put(PriveTalkTables.NotificationsTable.MESSAGE, this.message);
        contentValues.put(PriveTalkTables.NotificationsTable.TYPE, this.type);
        contentValues.put(PriveTalkTables.NotificationsTable.CREATED_ON, this.createdOn);
        contentValues.put(PriveTalkTables.NotificationsTable.RECEIVER, this.receiver);
        contentValues.put(PriveTalkTables.NotificationsTable.NOTIFICATION_READ, norificationRed);

        return contentValues;
    }

    public static TempNotification parseObjects(JSONArray array) {
        List<NotificationObject> objects = new ArrayList<>();
        String[] ids = new String[array.length()];

        for (int i = 0; i < array.length(); i++) {
            objects.add(new NotificationObject(array.optJSONObject(i)));
            ids[i] = String.valueOf(objects.get(i).id);
        }

        return new TempNotification(objects, ids);
    }
}
