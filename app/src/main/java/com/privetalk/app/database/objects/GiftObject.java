package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONObject;

/**
 * Created by zachariashad on 03/03/16.
 */
public class GiftObject {

    public int giftId;
    public int cost;
    public String altText;
    public String thumb;
    public int order;

    public GiftObject() {
    }

    public GiftObject(JSONObject obj, int order) {

        this.giftId = obj.optInt("id");
        this.cost = obj.optInt("cost");
        this.altText = obj.optString("alt_text");
        this.thumb = obj.optString("thumb");
        this.order = order;
    }

    public GiftObject(Cursor cursor) {
        this.giftId = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.GiftsTables.GIFT_ID));
        this.cost = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.GiftsTables.COST));
        this.altText = cursor.getString(cursor.getColumnIndex(PriveTalkTables.GiftsTables.ALT_TEXT));
        this.thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.GiftsTables.THUMB));
        this.order = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.GiftsTables.ORDER));
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.GiftsTables.GIFT_ID, this.giftId);
        contentValues.put(PriveTalkTables.GiftsTables.COST, this.cost);
        contentValues.put(PriveTalkTables.GiftsTables.ALT_TEXT, this.altText);
        contentValues.put(PriveTalkTables.GiftsTables.THUMB, this.thumb);
        contentValues.put(PriveTalkTables.GiftsTables.ORDER, this.order);

        return contentValues;
    }
}
