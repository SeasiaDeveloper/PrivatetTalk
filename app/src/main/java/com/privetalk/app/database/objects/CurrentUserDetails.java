package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zeniosagapiou on 04/02/16.
 */
public class CurrentUserDetails implements Serializable {

    public int id;

    public AttributesObject relationship_status = new AttributesObject();
    public AttributesObject sexuality_status = new AttributesObject();
    public AttributesObject smoking_status = new AttributesObject();
    public AttributesObject drinking_status = new AttributesObject();
    public AttributesObject education_status = new AttributesObject();
    public AttributesObject zodiac = new AttributesObject();
    public AttributesObject body_type = new AttributesObject();
    public AttributesObject hair_color = new AttributesObject();
    public AttributesObject eyes_color = new AttributesObject();
    public FaithObject faith = new FaithObject();

    public HashMap<String, ArrayList<InterestObject>> interests;

    public ArrayList<LanguageObject> languageObjects = new ArrayList<>();

    public int weight;
    public int height;
    public String about = "";

    public CurrentUserDetails() {
        this.interests = new HashMap<>();

        this.interests.put("a", new ArrayList<InterestObject>());
        this.interests.put("m", new ArrayList<InterestObject>());
        this.interests.put("mo", new ArrayList<InterestObject>());
        this.interests.put("l", new ArrayList<InterestObject>());
        this.interests.put("p", new ArrayList<InterestObject>());
        this.interests.put("o", new ArrayList<InterestObject>());
    }

    public CurrentUserDetails(JSONObject jsonObject) {

        this.id = jsonObject.optInt("id");

        JSONObject info = jsonObject.optJSONObject("info");

        if (info != null) {
            this.relationship_status = AttributesObject.getAttributeObject(info.optInt("relationship_status"), PriveTalkTables.AttributesTables.getTableString("relationship_status"));
            this.sexuality_status = AttributesObject.getAttributeObject(info.optInt("sexuality_status"), PriveTalkTables.AttributesTables.getTableString("sexuality_status"));
            this.smoking_status = AttributesObject.getAttributeObject(info.optInt("smoking_status"), PriveTalkTables.AttributesTables.getTableString("smoking_status"));
            this.drinking_status = AttributesObject.getAttributeObject(info.optInt("drinking_status"), PriveTalkTables.AttributesTables.getTableString("drinking_status"));
            this.education_status = AttributesObject.getAttributeObject(info.optInt("education_status"), PriveTalkTables.AttributesTables.getTableString("education_status"));
            this.zodiac = AttributesObject.getAttributeObject(info.optInt("zodiac"), PriveTalkTables.AttributesTables.getTableString("zodiac"));
            this.height = info.optInt("height");
            this.weight = info.optInt("weight");
            this.body_type = AttributesObject.getAttributeObject(info.optInt("body_type"), PriveTalkTables.AttributesTables.getTableString("body_type"));
            this.hair_color = AttributesObject.getAttributeObject(info.optInt("hair_color"), PriveTalkTables.AttributesTables.getTableString("hair_color"));
            this.eyes_color = AttributesObject.getAttributeObject(info.optInt("eyes_color"), PriveTalkTables.AttributesTables.getTableString("eyes_color"));
            this.about = info.optString("about");
        }

        this.faith = new FaithObject(jsonObject.optJSONObject("faith"));
        this.languageObjects = LanguageObject.getLanguagesListFromServer(jsonObject.optJSONArray("languages"));
        this.interests = InterestObject.getInterestsListFromServer(jsonObject.optJSONArray("interests"), true);
//        Log.d("interestsAttributes", String.valueOf(jsonObject.optJSONArray("interests")));
    }


    public CurrentUserDetails(Cursor cursor) {

        id = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.ID));
        relationship_status = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.RELATIONSHIP_STATUS)),
                PriveTalkTables.AttributesTables.getTableString("relationship_status"));
        sexuality_status = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.SEXUALITY_STATUS)),
                PriveTalkTables.AttributesTables.getTableString("sexuality_status"));
        smoking_status = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.SMOKING_STATUS)),
                PriveTalkTables.AttributesTables.getTableString("smoking_status"));
        drinking_status = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.DRINKING_STATUS)),
                PriveTalkTables.AttributesTables.getTableString("drinking_status"));
        education_status = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.EDUCATION_STATUS)),
                PriveTalkTables.AttributesTables.getTableString("education_status"));
        zodiac = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.ZODIAC)),
                PriveTalkTables.AttributesTables.getTableString("zodiac"));
        height = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.HEIGHT));
        weight = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.WEIGHT));
        body_type = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.BODY_TYPE)),
                PriveTalkTables.AttributesTables.getTableString("body_type"));
        hair_color = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.HAIR_COLOR)),
                PriveTalkTables.AttributesTables.getTableString("hair_color"));
        eyes_color = AttributesObject.getAttributeObject(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.EYES_COLOR)),
                PriveTalkTables.AttributesTables.getTableString("eyes_color"));
        about = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.ABOUT));

        faith = FaithObject.getFaithFromDatabase(cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.FAITH)));
        languageObjects = LanguageObject.getUserLanguagesFromDatabase(cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.LANGUAGES)));
        interests = InterestObject.getUserInterObjectFromDatabase(cursor.getString(cursor.getColumnIndex(PriveTalkTables.CurrentUserDetailsTable.INTERESTS)));

    }

    public static ContentValues getContentValues(CurrentUserDetails currentUserDetails) {

        ContentValues values = new ContentValues();

        values.put(PriveTalkTables.CurrentUserDetailsTable.ID, currentUserDetails.id);
        values.put(PriveTalkTables.CurrentUserDetailsTable.RELATIONSHIP_STATUS, currentUserDetails.relationship_status.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.SEXUALITY_STATUS, currentUserDetails.sexuality_status.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.SMOKING_STATUS, currentUserDetails.smoking_status.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.DRINKING_STATUS, currentUserDetails.drinking_status.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.EDUCATION_STATUS, currentUserDetails.education_status.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.ZODIAC, currentUserDetails.zodiac.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.HEIGHT, currentUserDetails.height);
        values.put(PriveTalkTables.CurrentUserDetailsTable.WEIGHT, currentUserDetails.weight);
        values.put(PriveTalkTables.CurrentUserDetailsTable.BODY_TYPE, currentUserDetails.body_type.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.HAIR_COLOR, currentUserDetails.hair_color.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.EYES_COLOR, currentUserDetails.eyes_color.value);
        values.put(PriveTalkTables.CurrentUserDetailsTable.ABOUT, currentUserDetails.about);
        values.put(PriveTalkTables.CurrentUserDetailsTable.FAITH, currentUserDetails.faith.getJSONObject().toString());
        values.put(PriveTalkTables.CurrentUserDetailsTable.LANGUAGES, LanguageObject.toJsonArrayString(currentUserDetails.languageObjects));
        values.put(PriveTalkTables.CurrentUserDetailsTable.INTERESTS, InterestObject.toJsonArrayString(currentUserDetails.interests));


        return values;
    }


    public String getStringFromList(ArrayList<String> list) {

        String string = "";

        String comma = "";
        for (String stringie : list) {
            string = string + comma + stringie;
            comma = ",";
        }

        if (string.isEmpty()) {
            string = PriveTalkApplication.getInstance().getString(R.string.unspecified);
        }

        return string;
    }

    public JSONObject getInfo() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("relationship_status", relationship_status.value);
        jsonObject.put("sexuality_status", sexuality_status.value);
        jsonObject.put("smoking_status", smoking_status.value);
        jsonObject.put("drinking_status", drinking_status.value);
        jsonObject.put("education_status", education_status.value);
        jsonObject.put("zodiac", zodiac.value);
        jsonObject.put("height", height);
        jsonObject.put("weight", weight);
        jsonObject.put("body_type", body_type.value);
        jsonObject.put("hair_color", hair_color.value);
        jsonObject.put("eyes_color", eyes_color.value);
        jsonObject.put("about", about);

        return jsonObject;
    }
}
