package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.utilities.PriveTalkUtilities;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by zachariashad on 02/03/16.
 */
public class MessageObject implements Serializable {

    public static final String TYPE_MESSAGE = "txt";
    public static final String TYPE_IMAGE = "img";
    public static final String TYPE_GIFT = "gift";
    public static final SimpleDateFormat format = new SimpleDateFormat("dd, MMM yyyy - HH:mm:ssZ", Locale.US);

    public int messageID;
    public int senderID;
    public int receiverID;
    public String mtype = "";
    public String data = "";
    public int lifespan;
    public long expiresOn;
    public long createdOn;
    public String attachmentData;
    public boolean can_vote;
    public boolean vote_casted;
    public boolean expired;
    public long globalReadTime;

    public MessageObject() {

    }


    public MessageObject(JSONObject obj, int currentUserId) {
        this.messageID = obj.optInt("id");
        this.senderID = obj.optInt("sender");
        this.receiverID = obj.optInt("receiver");
        this.mtype = obj.optString("mtype");
        this.data = obj.optString("data");
        this.lifespan = obj.optInt("lifespan");
        this.can_vote = obj.optBoolean("can_vote");
        this.vote_casted = obj.optBoolean("vote_casted");
        try {
            String created = obj.optString("created_on");
            this.createdOn = (!created.isEmpty() && !created.equals("null")) ? format.parse(created).getTime() : 0;

            String expire = obj.optString("expires_on");
            this.expiresOn = (!expire.isEmpty() && !expire.equals("null")) ? format.parse(expire).getTime() : 0;
            this.expiresOn = this.senderID == currentUserId ? this.expiresOn :
                    this.expiresOn != 0 ? this.expiresOn : System.currentTimeMillis() - PriveTalkUtilities.getGlobalTimeDifference() + (this.lifespan + 1) * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.attachmentData = obj.optString("attachment_data");
        this.globalReadTime = 0;
    }


    public MessageObject(Cursor cursor) {

        this.messageID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.MessagesTable.MSG_ID));
        this.senderID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.MessagesTable.SENDER_ID));
        this.receiverID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.MessagesTable.RECEIVER_ID));
        this.mtype = cursor.getString(cursor.getColumnIndex(PriveTalkTables.MessagesTable.MTYPE));
        this.data = cursor.getString(cursor.getColumnIndex(PriveTalkTables.MessagesTable.DATA));
        this.lifespan = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.MessagesTable.LIFESPAN));
        this.expiresOn = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.MessagesTable.EXPIRES_ON));
        this.createdOn = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.MessagesTable.CREATED_ON));
        this.attachmentData = cursor.getString(cursor.getColumnIndex(PriveTalkTables.MessagesTable.ATTACHEMENT_DATA));
        this.vote_casted = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.MessagesTable.VOTE_CASTED)) == 1;
        this.can_vote = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.MessagesTable.CAN_VOTE)) == 1;
        this.expired = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.MessagesTable.EXPIRED)) == 1;
        this.globalReadTime = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.MessagesTable.GLOBAL_TIME));
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.MessagesTable.MSG_ID, this.messageID);
        contentValues.put(PriveTalkTables.MessagesTable.SENDER_ID, this.senderID);
        contentValues.put(PriveTalkTables.MessagesTable.RECEIVER_ID, this.receiverID);
        contentValues.put(PriveTalkTables.MessagesTable.MTYPE, this.mtype);
        contentValues.put(PriveTalkTables.MessagesTable.DATA, this.data);
        contentValues.put(PriveTalkTables.MessagesTable.LIFESPAN, this.lifespan);
        contentValues.put(PriveTalkTables.MessagesTable.EXPIRES_ON, this.expiresOn);
        contentValues.put(PriveTalkTables.MessagesTable.CREATED_ON, this.createdOn);
        contentValues.put(PriveTalkTables.MessagesTable.ATTACHEMENT_DATA, this.attachmentData);
        contentValues.put(PriveTalkTables.MessagesTable.CAN_VOTE, this.can_vote ? 1 : 0);
        contentValues.put(PriveTalkTables.MessagesTable.VOTE_CASTED, this.vote_casted ? 1 : 0);
        contentValues.put(PriveTalkTables.MessagesTable.GLOBAL_TIME, this.globalReadTime);
        contentValues.put(PriveTalkTables.MessagesTable.EXPIRED, this.expired ? 1 : 0);

        return contentValues;
    }

    public static synchronized String getExpirationTime(long time) {

        int days = (int) (time / 86400000);
        time = time - (days * 86400000);
        int hours = (int) (time / 3600000);
        time = time - (hours * 3600000);
        int minutes = (int) (time / 60000);
        time = time - (minutes * 60000);
        int seconds = (int) (time / 1000);

        return (String.valueOf(days) + "{b}D{/b}" +
                ((hours >= 10) ? "" : "0") + String.valueOf(hours) + "{b}h{/b}" +
                ((minutes >= 10) ? "" : "0") + String.valueOf(minutes) + "{b}m{/b}" +
                ((seconds >= 10) ? "" : "0") + String.valueOf(seconds)) + "{b}s{/b}";

    }

}
