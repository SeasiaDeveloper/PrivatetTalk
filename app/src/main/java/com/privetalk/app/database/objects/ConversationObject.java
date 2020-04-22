package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.utilities.PriveTalkConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachariashad on 01/03/16.
 */
public class ConversationObject implements Serializable {

    public int partnerID;
    public String partnerName;
    public boolean isSenderRoyal;
    public int senderVerificationLevel;
    public String partnerAvatarImg;
    public int senderAge;
    public String description;
    public boolean isHotMatch;
    public long lastMessageTimeStamp;
    public boolean isConversationRead;
    public boolean canVote;
    public boolean vote_casted;

    public ConversationObject() {
    }


    public ConversationObject(JSONObject obj) {
        this.partnerID = obj.optInt("partner");
        this.partnerName = obj.optString("partner_name");
        this.isSenderRoyal = obj.optBoolean("sender_is_royal");
        this.senderVerificationLevel = obj.optInt("sender_verification_level");
        this.partnerAvatarImg = obj.optString("partner_avatar_img");
        this.senderAge = obj.optInt("sender_age");
        this.description = obj.optString("description");
        this.isHotMatch = obj.optBoolean("is_hot_match");
        this.isConversationRead = obj.optBoolean("is_conv_read");
        this.canVote = obj.optBoolean("can_vote");
        this.vote_casted = obj.optBoolean("vote_casted");
        try {
            this.lastMessageTimeStamp = PriveTalkConstants.conversationDateFormat.parse(obj.optString("last_msg_timestamp")).getTime();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }


    public ConversationObject(Cursor cursor) {

        this.partnerID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.PARTNER_ID));
        this.partnerName = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.PARTNER_NAME));
        this.isSenderRoyal = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.IS_ROYAL_USER)) == 1;
        this.senderVerificationLevel = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.VERIFICATION_LEVEL));
        this.partnerAvatarImg = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.AVATAR_IMG));
        this.senderAge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.AGE));
        this.description = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.DESCRIPTION));
        this.isHotMatch = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.IS_HOT_MATCH)) == 1;
        this.lastMessageTimeStamp = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.LAST_MSG_TIMESTAMP));
        this.isConversationRead = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.IS_READ)) == 1;
        this.canVote = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.CAN_VOTE)) == 1;
        this.vote_casted = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConversationsTable.VOTE_CASTED)) == 1;
    }

    public ContentValues getConversationContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.ConversationsTable.PARTNER_ID, this.partnerID);
        contentValues.put(PriveTalkTables.ConversationsTable.PARTNER_NAME, this.partnerName);
        contentValues.put(PriveTalkTables.ConversationsTable.IS_ROYAL_USER, this.isSenderRoyal ? 1 : 0);
        contentValues.put(PriveTalkTables.ConversationsTable.VERIFICATION_LEVEL, this.senderVerificationLevel);
        contentValues.put(PriveTalkTables.ConversationsTable.AVATAR_IMG, this.partnerAvatarImg);
        contentValues.put(PriveTalkTables.ConversationsTable.AGE, this.senderAge);
        contentValues.put(PriveTalkTables.ConversationsTable.DESCRIPTION, this.description);
        contentValues.put(PriveTalkTables.ConversationsTable.IS_HOT_MATCH, this.isHotMatch ? 1 : 0);
        contentValues.put(PriveTalkTables.ConversationsTable.LAST_MSG_TIMESTAMP, this.lastMessageTimeStamp);
        contentValues.put(PriveTalkTables.ConversationsTable.IS_READ, this.isConversationRead ? 1 : 0);
        contentValues.put(PriveTalkTables.ConversationsTable.CAN_VOTE, this.canVote ? 1 : 0);
        contentValues.put(PriveTalkTables.ConversationsTable.VOTE_CASTED, this.vote_casted ? 1 : 0);

        return contentValues;
    }

    public static synchronized List<ConversationObject> conversationParser(JSONArray array) {

        List<ConversationObject> objects = new ArrayList<>();

        for (int i = 0; i < array.length(); i++)
            objects.add(new ConversationObject(array.optJSONObject(i)));

        return objects;
    }


}
