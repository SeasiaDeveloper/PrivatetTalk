package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.utilities.PriveTalkConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachariashad on 24/02/16.
 */
public class PromotedUsersObject {

    public int objectID;
    public int promotionID;
    public int id;
    public String name;
    public long birthday;
    public int age;
    public boolean isOnline;
    public int photoID;
    public String photo;
    public String thumb;
    public String squarePhoto;
    public String squareThumb;
    public boolean isMainProfilePhoto;
    public long purchaseDate;

    public PromotedUsersObject() {
    }

    public PromotedUsersObject(PromotedUsersObject obj, JSONObject jsonObject) {
        this.objectID = jsonObject.optInt("id");
        this.promotionID = obj.promotionID;
        this.id = obj.id;
        this.name = obj.name;
        this.birthday = obj.birthday;
        this.age = obj.age;
        this.isOnline = obj.isOnline;
        this.photoID = obj.photoID;
        this.photo = obj.photo;
        this.thumb = obj.thumb;
        this.squarePhoto = obj.squarePhoto;
        this.squareThumb = obj.squareThumb;
        this.isMainProfilePhoto = obj.isMainProfilePhoto;
        try {
            this.purchaseDate = PriveTalkConstants.conversationDateFormat.parse(jsonObject.optString("purchase_date")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public PromotedUsersObject(Cursor cursor) {
        this.objectID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.OBJECT_ID));
        this.promotionID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.PROMOTION_ID));
        this.id = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.USER_ID));
        this.name = cursor.getString(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.NAME));
        this.birthday = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.BIRTDAY));
        this.age = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.AGE));
        this.isOnline = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.IS_ONLINE)) == 1;
        this.photoID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.PHOTO_ID));
        this.photo = cursor.getString(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.PHOTO));
        this.thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.THUMB));
        this.squarePhoto = cursor.getString(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.SQUARE_PHOTO));
        this.squareThumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.SQUARE_THUMB));
        this.isMainProfilePhoto = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.IS_MAIN_PROFILE_PHOTO)) == 1;
        this.purchaseDate = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.PromotedUsersTable.PROMOTION_DATE));
    }


    public ContentValues getPromotedUsersContentValues() {
        ContentValues promotedUsers = new ContentValues();

        promotedUsers.put(PriveTalkTables.PromotedUsersTable.OBJECT_ID, this.objectID);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.PROMOTION_ID, this.promotionID);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.USER_ID, this.id);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.NAME, this.name);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.BIRTDAY, this.birthday);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.AGE, this.age);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.IS_ONLINE, this.isOnline ? 1 : 0);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.PHOTO_ID, this.photoID);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.PHOTO, this.photo);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.THUMB, this.thumb);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.SQUARE_PHOTO, this.squarePhoto);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.SQUARE_THUMB, this.squareThumb);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.IS_MAIN_PROFILE_PHOTO, this.isMainProfilePhoto);
        promotedUsers.put(PriveTalkTables.PromotedUsersTable.PROMOTION_DATE, this.purchaseDate);

        return promotedUsers;
    }



    public synchronized static List<PromotedUsersObject> parsePromotedUsers(JSONArray jsonArray) {
        List<PromotedUsersObject> promotedUsersObjectsList = new ArrayList<>();
        JSONObject obj;
        JSONObject profileObj;
        JSONArray datesArray;
        JSONObject dateObj;
        PromotedUsersObject promotedUsersObject;

        for (int i = 0; i < jsonArray.length(); i++) {
            obj = jsonArray.optJSONObject(i);

            profileObj = obj.optJSONObject("profile");

            promotedUsersObject = new PromotedUsersObject();

            promotedUsersObject.promotionID = obj.optInt("id");
            promotedUsersObject.id = profileObj.optInt("id");
            promotedUsersObject.name = profileObj.optString("name");
            try {
                promotedUsersObject.birthday = PriveTalkConstants.BIRTHDAY_FORMAT.parse(profileObj.optString("birthday")).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            promotedUsersObject.age = profileObj.optInt("age");
            promotedUsersObject.isOnline = profileObj.optBoolean("is_online");

            promotedUsersObject.photo = obj.optString("photo");
            promotedUsersObject.thumb = obj.optString("thumb");
            promotedUsersObject.squarePhoto = obj.optString("square_photo");
            promotedUsersObject.squareThumb = obj.optString("square_thumb");
            promotedUsersObject.isMainProfilePhoto = obj.optBoolean("is_main_profile_photo");

            datesArray = obj.optJSONArray("promotion_dates");
            for (int j = 0; j < datesArray.length(); j++) {
                dateObj = datesArray.optJSONObject(j);
                promotedUsersObjectsList.add(new PromotedUsersObject(promotedUsersObject, dateObj));
            }
        }

        return promotedUsersObjectsList;
    }

}
