package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zachariashad on 25/02/16.
 */
public class ProfilePhoto implements Serializable {

    public int photoID;

    public String photo = "";
    public String thumb= "";
    public String medium_thumb= "";
    public String large_thumb= "";

    public String square_photo= "";
    public String square_thumb= "";
    public String medium_square_thumb= "";
    public String large_square_thumb= "";

    public boolean isMainProfilePhoto;

    public boolean isVerifiedPhoto;

    public ProfilePhoto() {

    }

    public ProfilePhoto(JSONObject jsonObject) {

        this.photoID = jsonObject.optInt("id");

        photo = jsonObject.optString("photo");
        thumb = jsonObject.optString("thumb");
        medium_thumb = jsonObject.optString("medium_thumb");
        large_thumb = jsonObject.optString("large_thumb");

        square_photo = jsonObject.optString("square_photo");
        square_thumb = jsonObject.optString("square_thumb");
        medium_square_thumb = jsonObject.optString("medium_square_thumb");
        large_square_thumb = jsonObject.optString("large_square_thumb");

        Log.d("zenios", square_photo);

        this.isMainProfilePhoto = jsonObject.optBoolean("is_main_profile_photo");
        this.isVerifiedPhoto=jsonObject.optBoolean("verified_photo");
    }

    public ProfilePhoto(Cursor cursor) {

        photoID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.PHOTO_ID));
        photo = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.PHOTO));
        thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.THUMB));
        medium_thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.MEDIUM_THUMB));
        large_thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.LARGE_THUMB));

        square_photo = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.SQUARE_PHOTO));
        square_thumb =cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.SQUARE_THUMB));
        medium_square_thumb = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.MEDIUM_SQUARE_THUMB));
        large_square_thumb =cursor.getString(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.LARGE_SQUARE_THUMB));

        isMainProfilePhoto = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.IS_MAIN_PROFILE_PHOTO)) > 0;
        isVerifiedPhoto = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CommunityUsersTable.IS_MAIN_PROFILE_PHOTO)) > 0;

    }

    public void addContentValues(ContentValues contentValues) {

        contentValues.put(PriveTalkTables.HotMatchesTable.PHOTO_ID, photoID);
        contentValues.put(PriveTalkTables.HotMatchesTable.PHOTO, photo);
        contentValues.put(PriveTalkTables.HotMatchesTable.THUMB, thumb);
        contentValues.put(PriveTalkTables.HotMatchesTable.MEDIUM_THUMB, medium_thumb);
        contentValues.put(PriveTalkTables.HotMatchesTable.LARGE_THUMB, large_thumb);

        contentValues.put(PriveTalkTables.HotMatchesTable.SQUARE_PHOTO, square_photo);
        contentValues.put(PriveTalkTables.HotMatchesTable.SQUARE_THUMB, square_thumb);
        contentValues.put(PriveTalkTables.HotMatchesTable.MEDIUM_SQUARE_THUMB, medium_square_thumb);
        contentValues.put(PriveTalkTables.HotMatchesTable.LARGE_SQUARE_THUMB, large_square_thumb);

        contentValues.put(PriveTalkTables.HotMatchesTable.IS_MAIN_PROFILE_PHOTO, isMainProfilePhoto);

    }


}
