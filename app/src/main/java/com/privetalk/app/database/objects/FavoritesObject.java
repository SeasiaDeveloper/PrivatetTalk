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
 * Created by zachariashad on 25/02/16.
 */
public class FavoritesObject {

    public int userID;
    public String userName;
    public long userBirthdate;
    public int userAge;
    //    public String verifiedPhotoFront;
    public boolean isOnline;
    public boolean socialVerified;
    public boolean royalUser;
    public boolean photosVerified;
    public MainProfilePhoto mainProfilePhoto;
    public boolean isFavorite;
//    public boolean isFavoriteBy;
    public long lastUpdate;
    public int profile_;
    public int favoriteBy;


    public FavoritesObject() {
    }


    public FavoritesObject(JSONObject object, boolean isFavorite) {
        mainProfilePhoto = new MainProfilePhoto();
        JSONObject obj;
        if (isFavorite)
            obj = object.optJSONObject("profile");
        else
            obj = object.optJSONObject("favorited_by");

        this.isFavorite = isFavorite;

        userID = obj.optInt("id");
        userName = obj.optString("name");
        try {
            userBirthdate = PriveTalkConstants.COMMUNITY_USERS_DATE_FORMAT.parse(obj.optString("birthday")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        userAge = obj.optInt("age");
        isOnline = obj.optBoolean("is_online");
        socialVerified = obj.optBoolean("social_verified");
        royalUser = obj.optBoolean("royal_user");
        photosVerified = obj.optBoolean("photos_verified");

        JSONObject photoObj = obj.optJSONObject("main_profile_photo");

        if (photoObj != null) {
            mainProfilePhoto.id = photoObj.optInt("id");
            mainProfilePhoto.photo = photoObj.optString("photo");
            mainProfilePhoto.thumb = photoObj.optString("thumb");
            mainProfilePhoto.squarePhoto = photoObj.optString("square_photo");
            mainProfilePhoto.squareThumb = photoObj.optString("square_thumb");
            mainProfilePhoto.isMainProfilePhoto = photoObj.optBoolean("is_main_profile_photo");
        }

//        try {
//            lastUpdate = PriveTalkConstants.PROMOTION_DATE.parse("last_update").getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        if (isFavorite)
            favoriteBy = object.optInt("favorited_by");
        else
            profile_ = object.optInt("profile");


    }

    public FavoritesObject(Cursor cursor) {

        mainProfilePhoto = new MainProfilePhoto();

        userID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.USER_ID));
        userName = cursor.getString(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.NAME));
        userBirthdate = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.BIRTDATE));
        userAge = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.AGE));
        isOnline = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.IS_ONLINE)) == 1);
        socialVerified = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.SOCIAL_VERIFIED)) == 1);
        royalUser = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.ROYAL_USER)) == 1);
        photosVerified = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.PHOTOS_VERIFIED)) == 1);
        mainProfilePhoto.id = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.PHOTO_ID)));
        mainProfilePhoto.photo = (cursor.getString(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.PHOTO)));
        mainProfilePhoto.thumb = (cursor.getString(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.THUMB)));
        mainProfilePhoto.squarePhoto = (cursor.getString(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.SQUARE_PHOTO)));
        mainProfilePhoto.squareThumb = (cursor.getString(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.SQUARE_THUMB)));
        mainProfilePhoto.isMainProfilePhoto = (cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.IS_MAIN_PROFILE_PHOTO)) == 1);
        isFavorite = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.IS_FAVORITE)) == 1;
        lastUpdate = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.LAST_UPDATE));
        profile_ = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.PROFILE));
        favoriteBy = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.FAVORITE_BY));
//        isFavoriteBy = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.FavoritesTable.IS_FAVORITE_BY)) == 1;


    }

    public ContentValues getFavoritesObjectContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.FavoritesTable.USER_ID, this.userID);
        contentValues.put(PriveTalkTables.FavoritesTable.NAME, this.userName);
        contentValues.put(PriveTalkTables.FavoritesTable.BIRTDATE, this.userBirthdate);
        contentValues.put(PriveTalkTables.FavoritesTable.AGE, this.userAge);
        contentValues.put(PriveTalkTables.FavoritesTable.IS_ONLINE, this.isOnline ? 1 : 0);
        contentValues.put(PriveTalkTables.FavoritesTable.SOCIAL_VERIFIED, this.socialVerified ? 1 : 0);
        contentValues.put(PriveTalkTables.FavoritesTable.ROYAL_USER, this.royalUser ? 1 : 0);
        contentValues.put(PriveTalkTables.FavoritesTable.PHOTOS_VERIFIED, this.photosVerified ? 1 : 0);
        contentValues.put(PriveTalkTables.FavoritesTable.PHOTO_ID, this.mainProfilePhoto.id);
        contentValues.put(PriveTalkTables.FavoritesTable.PHOTO, this.mainProfilePhoto.photo);
        contentValues.put(PriveTalkTables.FavoritesTable.THUMB, this.mainProfilePhoto.squarePhoto);
        contentValues.put(PriveTalkTables.FavoritesTable.SQUARE_PHOTO, this.mainProfilePhoto.squarePhoto);
        contentValues.put(PriveTalkTables.FavoritesTable.SQUARE_THUMB, this.mainProfilePhoto.squareThumb);
        contentValues.put(PriveTalkTables.FavoritesTable.IS_MAIN_PROFILE_PHOTO, this.mainProfilePhoto.isMainProfilePhoto ? 1 : 0);
        contentValues.put(PriveTalkTables.FavoritesTable.IS_FAVORITE, this.isFavorite ? 1 : 0);
        contentValues.put(PriveTalkTables.FavoritesTable.LAST_UPDATE, this.lastUpdate);
        contentValues.put(PriveTalkTables.FavoritesTable.PROFILE, this.profile_);
        contentValues.put(PriveTalkTables.FavoritesTable.FAVORITE_BY, this.favoriteBy);

        return contentValues;
    }


    public class MainProfilePhoto {

        public MainProfilePhoto() {
        }

        public int id;
        public String photo;
        public String thumb;
        public String squarePhoto;
        public String squareThumb;
        public boolean isMainProfilePhoto;


    }



    public static synchronized List<FavoritesObject> favoritesObjectsParser(JSONArray array, boolean isFavorites) {
        List<FavoritesObject> objects = new ArrayList<>();

        for (int i = 0; i < array.length(); i++)
            objects.add(new FavoritesObject(array.optJSONObject(i), isFavorites));

        return objects;
    }


}
