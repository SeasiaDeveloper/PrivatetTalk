package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONObject;

/**
 * Created by zachariashad on 21/04/16.
 */
public class BadgesObject {
    public int id;
    public int visitorsBadge;
    public int messagesBadge;
    public int flamesBadge;
    public int matchesBadge;
    public int favouritedBadge;


    public BadgesObject(){

    }

    public BadgesObject(JSONObject obj) {
        this.id = obj.optInt("id");
        this.visitorsBadge = obj.optInt("visitors_badge");
        this.messagesBadge = obj.optInt("messages_badge");
        this.flamesBadge = obj.optInt("flames_badge");
        this.matchesBadge = obj.optInt("matches_badge");
        this.favouritedBadge = obj.optInt("favourited_badge");
    }


    public BadgesObject(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.BadgesTable.ID));
        this.visitorsBadge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.BadgesTable.VISITORS));
        this.messagesBadge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.BadgesTable.MESSAGES));
        this.flamesBadge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.BadgesTable.FLAMES));
        this.matchesBadge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.BadgesTable.MATCHES));
        this.favouritedBadge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.BadgesTable.FAVORITES));
    }


    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.BadgesTable.ID, this.id);
        contentValues.put(PriveTalkTables.BadgesTable.VISITORS, this.visitorsBadge);
        contentValues.put(PriveTalkTables.BadgesTable.MESSAGES, this.messagesBadge);
        contentValues.put(PriveTalkTables.BadgesTable.FLAMES, this.flamesBadge);
        contentValues.put(PriveTalkTables.BadgesTable.MATCHES, this.matchesBadge);
        contentValues.put(PriveTalkTables.BadgesTable.FAVORITES, this.favouritedBadge);

        return contentValues;
    }


}
