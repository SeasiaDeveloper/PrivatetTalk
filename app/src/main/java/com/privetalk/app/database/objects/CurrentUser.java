package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.plus.model.people.Person;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zachariashad on 11/01/16.
 */
public class CurrentUser implements Serializable {

    public static final int MALE = 1;
    public static final int FEMALE = 2;

    public int userID = 0;
    public String name = "";
    public AttributesObject gender = new AttributesObject();
    public int lookingFor;
    public long birthday;
    public String location = "";
    public int coins;
    public boolean royal_user;

    public CurrentUserPhotoObject main_profile_photo = new CurrentUserPhotoObject();

    public boolean name_edited;
    public boolean birthday_edited;
    public boolean photos_verified;
    public boolean social_verified;

    public int photos_verified_rejected;

    public String fb_id = "";
    public String fb_username = "";
    public String gplus_id = "";
    public String vk_id = "";

    public String verified_photo_front = "";
    public String verified_photo_left = "";
    public String verified_photo_right = "";

    public int last_week_purchase_bonus;
    public boolean last_week_purchase_bonus_used;
    public int this_week_profile_visits;
    public int this_week_conversations;
    public double this_week_reply_percentage;
    public int this_week_votes_casted;
    public int this_week_positive_votes;
    public int this_week_free_coins;
    public int this_week_purchase_bonus;
    public double hotness_percentage;
    public boolean promoted;
    public boolean hidden_mode_on;
    public boolean sharedApp;


//    public String verified_photo_front, verified_photo_left, verified_photo_right;

    public CurrentUserDetails currentUserDetails = new CurrentUserDetails();
    public boolean prv_hide_public_search;
    public String email = "";
    public String token = "";
    public String lastLogin;

    public CurrentUser(Cursor cursor) {
        main_profile_photo = new CurrentUserPhotoObject();
        main_profile_photo.id = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.PROFILE_PHOTO));
        this.photos_verified_rejected = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.PHOTOS_VERIFIED_REJECTED));
        this.userID = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.ID));
        royal_user = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.ROYAL_USER)) > 0;
        this_week_profile_visits = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.THIS_WEEK_PROFILE_VISITS));
        currentUserDetails = CurrentUserDetailsDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserDetails();
        name = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.NAME));
        gender = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.GENRE)), PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.GENDERS]);
        lookingFor = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.LOOKING_FOR));
        birthday = cursor.getLong(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.BIRTHDAY));
        location = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.LOCATION));
        prv_hide_public_search = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.PRV_HIDE_PUBLIC_SEARCH)) > 0;
        coins = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.COINS));
        photos_verified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.PHOTOS_VERIFIED)) > 0;
        social_verified = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.SOCIAL_VERIFIED)) > 0;
        fb_id = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.FB_ID));
        fb_username = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.FB_USERNAME));
        gplus_id = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.GPLUS_ID));
        vk_id = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.VK_ID));
        verified_photo_front = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.VERIFIED_PHOTO_FRONT));
        verified_photo_left = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.VERIFIED_PHOTO_LEFT));
        verified_photo_right = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.VERIFIED_PHOTO_RIGHT));
        email = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.MAIL));
        token = PriveTalkUtilities.decrypt(cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.TOKEN)));
        this_week_conversations = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.THIS_WEEK_CONVERSATIONS));
        this_week_reply_percentage = cursor.getDouble(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.THIS_WEEK_REPLY_PERCENTAGE));
        this_week_votes_casted = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.THIS_WEEK_VOTES_CASTED));
        this_week_positive_votes = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.THIS_WEEK_POSITIVE_VOTES));
        this_week_free_coins = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.THIS_WEEK_FREE_COINS));
        this_week_purchase_bonus = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.THIS_WEEK_PURCHASE_BONUS));
        hotness_percentage = cursor.getDouble(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.HOTNESS_PERCENTAGE));
        promoted = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.PROMOTED)) > 0;
        last_week_purchase_bonus = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.LAST_WEEK_PURCHASE_BONUS));
        last_week_purchase_bonus_used = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.LAST_WEEK_PURCHASE_BONUS_USED)) > 0;
        name_edited = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.NAME_EDITED)) > 0;
        birthday_edited = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.BIRTHDAY_EDITED)) > 0;
        hidden_mode_on = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.HIDDEN_MODE_ON)) > 0;
        lastLogin = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.LAST_LOGIN));

        //shared_app_tag
        //sharedApp = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserTable.SHARED_APP))>0;
    }

    public CurrentUser(JSONObject object) {

        this.name = object.optString("name");
        this.email = object.optString("email");
        this.fb_id = object.optString("id");
        this.fb_username = object.optString("name");

//        this.verified_photo_front = object.optString("verified_photo_front");
//        this.verified_photo_left = object.optString("verified_photo_left");
//        this.verified_photo_right = object.optString("verified_photo_right");

//        System.out.println("");
        this.lookingFor = object.optInt("looking_for");

        String gender = object.optString("gender");
        if (gender != null) {
            if (gender.equals("male"))
                this.gender = AttributesObject.getAttributeObject(MALE, PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.GENDERS]);
            else if (gender.equals("female"))
                this.gender = AttributesObject.getAttributeObject(FEMALE, PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.GENDERS]);
            else
                this.gender = new AttributesObject();
        }
//shared_app_tag
//        try {
//            sharedApp = object.getBoolean("shared_app");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        JSONObject locationObject = object.optJSONObject("location");
        if (locationObject != null)
            this.location = locationObject.optString("name");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            this.birthday = simpleDateFormat.parse(object.optString("birthday")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        this.fb_id = object.optString("id");
//        this.fb_username = object.optString("name");
    }

    public CurrentUser(JSONObject response, String token, String mail) {

//        System.out.println("RESPONSE: " + response);

        this.userID = response.optInt("id");
        this.royal_user = response.optBoolean("royal_user");
        this.this_week_profile_visits = response.optInt("this_week_profile_visits");

        this.currentUserDetails = new CurrentUserDetails(response);
        this.name = response.optString("name");
        this.gender = AttributesObject.getAttributeObject(response.optInt("gender"), PriveTalkTables.AttributesTables.getTableString("gender"));
        this.lookingFor = response.optInt("looking_for");
        this.birthday = getDateFromString(response.optString("birthday")).getTime();
        this.location = response.optString("location");
        this.prv_hide_public_search = response.optBoolean("PRV_HIDE_PUBLIC_SEARCH");
        this.coins = response.optInt("coins");

        Log.d("testing50", " coins in object " + coins);

        this.photos_verified = response.optBoolean("photos_verified");
        this.photos_verified_rejected = response.optInt("photos_verified_rejected");
        this.social_verified = response.optBoolean("social_verified");
        this.fb_id = response.optString("fb_id");
        this.fb_username = response.optString("fb_username");
        this.gplus_id = response.optString("gplus_id");
        this.vk_id = response.optString("vk_id");
        this.verified_photo_front = response.optString("verified_photo_front");
        this.verified_photo_left = response.optString("verified_photo_left");
        this.verified_photo_right = response.optString("verified_photo_right");

        this.token = token;
        this.email = mail;

        this.this_week_conversations = response.optInt("this_week_conversations");
        this.this_week_reply_percentage = response.optDouble("this_week_reply_percentage");
        this.this_week_votes_casted = response.optInt("this_week_votes_casted");
        this.this_week_positive_votes = response.optInt("this_week_positive_votes");
        this.this_week_free_coins = response.optInt("this_week_free_coins");
        this.this_week_purchase_bonus = response.optInt("this_week_purchase_bonus");
        this.hotness_percentage = response.optDouble("hotness_percentage");
        this.promoted = response.optBoolean("promoted");
        this.last_week_purchase_bonus = response.optInt("last_week_purchase_bonus");
        this.last_week_purchase_bonus_used = response.optBoolean("last_week_purchase_bonus_used");
        this.name_edited = response.optBoolean("name_edited");
        this.birthday_edited = response.optBoolean("birthday_edited");

        this.main_profile_photo = new CurrentUserPhotoObject(response.optJSONObject("main_profile_photo"));
        this.hidden_mode_on = response.optBoolean("hidden_mode_on");
        this.lastLogin = response.optString("last_login");

        //shared_app_tag
        PriveTalkApplication.getInstance().getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(PriveTalkConstants.CLAIM_REWARD, response.optBoolean("shared_app")).apply();


    }

    public CurrentUser(JSONObject obj, String email) {

        this.vk_id = String.valueOf(obj.optString("id"));
        this.name = obj.optString("first_name") + obj.optString("last_name");
        this.email = email;

        int genderInt = obj.optInt("sex");

        if (genderInt == 2)
            this.gender = AttributesObject.getAttributeObject(MALE, PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.GENDERS]);
        else if (genderInt == 1)
            this.gender = AttributesObject.getAttributeObject(FEMALE, PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.GENDERS]);
        else
            this.gender = new AttributesObject();

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        try {
            this.birthday = format.parse(obj.optString("bdate")).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CurrentUser(Person currentPerson, String email) {

        //get gender
        if (currentPerson.hasGender())
            this.gender = AttributesObject.getAttributeObject(currentPerson.getGender(), PriveTalkTables.AttributesTables.TABLES_NAME[PriveTalkTables.AttributesTables.GENDERS]);
        else
            this.gender = new AttributesObject();


        //get birthday
        if (currentPerson.hasBirthday()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                this.birthday = dateFormat.parse(currentPerson.getBirthday()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //get name
        if (currentPerson.hasDisplayName())
            this.name = currentPerson.getDisplayName();

        //get location
        if (currentPerson.hasCurrentLocation())
            this.location = currentPerson.getCurrentLocation();

        //get mail
        this.email = email;

        this.gplus_id = currentPerson.getId();

    }

    public CurrentUser() {
        this.currentUserDetails = new CurrentUserDetails();

    }

    public ContentValues getContentValues() {

        ContentValues values = new ContentValues();

        values.put(PriveTalkTables.CurrentUserTable.ID, this.userID);
        values.put(PriveTalkTables.CurrentUserTable.NAME, this.name);
        values.put(PriveTalkTables.CurrentUserTable.GENRE, this.gender != null ? this.gender.value : null);
        values.put(PriveTalkTables.CurrentUserTable.LOOKING_FOR, this.lookingFor);
        values.put(PriveTalkTables.CurrentUserTable.BIRTHDAY, this.birthday);
        values.put(PriveTalkTables.CurrentUserTable.LOCATION, this.location);
        values.put(PriveTalkTables.CurrentUserTable.PRV_HIDE_PUBLIC_SEARCH, this.prv_hide_public_search);
        values.put(PriveTalkTables.CurrentUserTable.COINS, this.coins);
        values.put(PriveTalkTables.CurrentUserTable.PHOTOS_VERIFIED, this.photos_verified ? 1 : 0);
        values.put(PriveTalkTables.CurrentUserTable.SOCIAL_VERIFIED, this.social_verified ? 1 : 0);
        values.put(PriveTalkTables.CurrentUserTable.FB_ID, this.fb_id);
        values.put(PriveTalkTables.CurrentUserTable.FB_USERNAME, this.fb_username);
        values.put(PriveTalkTables.CurrentUserTable.GPLUS_ID, this.gplus_id);
        values.put(PriveTalkTables.CurrentUserTable.VK_ID, this.vk_id);
        values.put(PriveTalkTables.CurrentUserTable.VERIFIED_PHOTO_FRONT, this.verified_photo_front);
        values.put(PriveTalkTables.CurrentUserTable.VERIFIED_PHOTO_LEFT, this.verified_photo_left);
        values.put(PriveTalkTables.CurrentUserTable.VERIFIED_PHOTO_RIGHT, this.verified_photo_right);
        values.put(PriveTalkTables.CurrentUserTable.MAIL, this.email);
        values.put(PriveTalkTables.CurrentUserTable.TOKEN, PriveTalkUtilities.encrypt(this.token));
        values.put(PriveTalkTables.CurrentUserTable.PHOTOS_VERIFIED_REJECTED, this.photos_verified_rejected);

        values.put(PriveTalkTables.CurrentUserTable.THIS_WEEK_PROFILE_VISITS, this.this_week_profile_visits);
        values.put(PriveTalkTables.CurrentUserTable.THIS_WEEK_CONVERSATIONS, this.this_week_conversations);
        values.put(PriveTalkTables.CurrentUserTable.THIS_WEEK_REPLY_PERCENTAGE, this.this_week_reply_percentage);
        values.put(PriveTalkTables.CurrentUserTable.THIS_WEEK_VOTES_CASTED, this.this_week_votes_casted);
        values.put(PriveTalkTables.CurrentUserTable.THIS_WEEK_POSITIVE_VOTES, this.this_week_positive_votes);
        values.put(PriveTalkTables.CurrentUserTable.THIS_WEEK_FREE_COINS, this.this_week_free_coins);
        values.put(PriveTalkTables.CurrentUserTable.THIS_WEEK_PURCHASE_BONUS, this.this_week_purchase_bonus);
        values.put(PriveTalkTables.CurrentUserTable.HOTNESS_PERCENTAGE, this.hotness_percentage);

        values.put(PriveTalkTables.CurrentUserTable.PROMOTED, this.promoted);
        values.put(PriveTalkTables.CurrentUserTable.LAST_WEEK_PURCHASE_BONUS, this.last_week_purchase_bonus);
        values.put(PriveTalkTables.CurrentUserTable.LAST_WEEK_PURCHASE_BONUS_USED, this.last_week_purchase_bonus_used);
        values.put(PriveTalkTables.CurrentUserTable.NAME_EDITED, this.name_edited);
        values.put(PriveTalkTables.CurrentUserTable.BIRTHDAY_EDITED, this.birthday_edited);
        values.put(PriveTalkTables.CurrentUserTable.ROYAL_USER, this.royal_user);
        values.put(PriveTalkTables.CurrentUserTable.PROFILE_PHOTO, this.main_profile_photo == null ? -1 : this.main_profile_photo.id);
        values.put(PriveTalkTables.CurrentUserTable.HIDDEN_MODE_ON, this.hidden_mode_on);
        values.put(PriveTalkTables.CurrentUserTable.LAST_LOGIN, this.lastLogin);

        //shared_app_tag
        //values.put(PriveTalkTables.CurrentUserTable.SHARED_APP, this.sharedApp);

        return values;
    }

    public synchronized Date getDateFromString(String string) {
        try {
            return PriveTalkConstants.BIRTHDAY_FORMAT.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date(System.currentTimeMillis());
    }

    public JSONObject getJsonForServer() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("name", name);
            jsonObject.put("gender", gender.value);
            jsonObject.put("looking_for", lookingFor);
            jsonObject.put("birthday", new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date(birthday)));
            jsonObject.put("location", location);
            jsonObject.put("coins", coins);
            jsonObject.put("royal_user", royal_user);
            jsonObject.put("name_edited", name_edited);
            jsonObject.put("birthday_edited", birthday_edited);
            jsonObject.put("photos_verified", photos_verified);
            jsonObject.put("social_verified", social_verified);
            jsonObject.put("fb_id", fb_id);
            jsonObject.put("fb_username", fb_username);

            jsonObject.put("gplus_id", gplus_id);
            jsonObject.put("vk_id", vk_id);

            jsonObject.put("last_week_purchase_bonus", last_week_purchase_bonus);
            jsonObject.put("last_week_purchase_bonus_used", last_week_purchase_bonus_used);
            jsonObject.put("this_week_profile_visits", this_week_profile_visits);
            jsonObject.put("this_week_conversations", this_week_conversations);

            jsonObject.put("this_week_reply_percentage", this_week_reply_percentage);
            jsonObject.put("this_week_votes_casted", this_week_votes_casted);
            jsonObject.put("this_week_positive_votes", this_week_positive_votes);
            jsonObject.put("this_week_free_coins", this_week_free_coins);
            jsonObject.put("this_week_purchase_bonus", this_week_purchase_bonus);
            jsonObject.put("hotness_percentage", hotness_percentage);
            jsonObject.put("promoted", promoted);
            jsonObject.put("hidden_mode_on", hidden_mode_on);
            jsonObject.put("last_login", lastLogin);

            JSONObject info = currentUserDetails.getInfo();
            jsonObject.put("info", info);

            //shared_app_tag
            //jsonObject.put("shared_app", sharedApp);

            JSONArray languageJsonArray = new JSONArray(LanguageObject.toJsonArrayString(currentUserDetails.languageObjects));
            jsonObject.put("languages", languageJsonArray);

            if (!currentUserDetails.faith.religion.value.equals("0")) {
                JSONObject faith = currentUserDetails.faith.getJSONObject();
                jsonObject.put("faith", faith);
            }

            JSONArray interestArray = new JSONArray(InterestObject.toJsonArrayString(currentUserDetails.interests));
            jsonObject.put("interests", interestArray);

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return jsonObject;
    }
}
