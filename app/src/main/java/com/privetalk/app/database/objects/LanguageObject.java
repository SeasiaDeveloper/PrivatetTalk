package com.privetalk.app.database.objects;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 15/02/16.
 */
public class LanguageObject implements Serializable{

    public static final String JSONKEY_LANGUAGE = "language";
    public static final String JSON_SPEAKING_LEVEL = "level";

    public AttributesObject language;
    public AttributesObject speakingLevel;

    public LanguageObject(JSONObject singleLanguageObjectFromServer) {
        language = AttributesObject.getAttributeObject(singleLanguageObjectFromServer.optString("language"), PriveTalkTables.AttributesTables.getTableString("languages"));
        speakingLevel = AttributesObject.getAttributeObject(singleLanguageObjectFromServer.optString("level"), PriveTalkTables.AttributesTables.getTableString("speaking_levels"));
    }

    public LanguageObject(AttributesObject languageObject, AttributesObject speakingLevelObject) {
        this.language = languageObject;
        this.speakingLevel = speakingLevelObject;
    }

    public static ArrayList<LanguageObject> getLanguagesListFromServer(JSONArray jsonArray) {

        ArrayList<LanguageObject> languageObjectArrayList = new ArrayList<>();

        if (jsonArray == null)
            return languageObjectArrayList;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                languageObjectArrayList.add(new LanguageObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return languageObjectArrayList;
    }

    public JSONObject getJSONObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONKEY_LANGUAGE, language.value);
            jsonObject.put(JSON_SPEAKING_LEVEL, speakingLevel.value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static ArrayList<LanguageObject> getUserLanguagesFromDatabase(String jsonArrayString) {

        ArrayList<LanguageObject> languageObjectArrayList = new ArrayList<>();

        try {
            JSONArray languagesArray = new JSONArray(jsonArrayString);

            for (int i = 0; i < languagesArray.length(); i++) {
                languageObjectArrayList.add(new LanguageObject(languagesArray.getJSONObject(i)));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return languageObjectArrayList;

    }


    public static String toJsonArrayString(ArrayList<LanguageObject> languageObjects) {

        JSONArray jsonArray = new JSONArray();

        for (LanguageObject object : languageObjects) {
            jsonArray.put(object.getJSONObject());
        }

        return jsonArray.toString();
    }

    public static String getStringFromList(ArrayList<LanguageObject> list) {

        String string = "";

        String comma = "";
        for (LanguageObject languageObject : list) {
            string = string + comma + languageObject.language.display + " (" + languageObject.speakingLevel.display + ")";
            comma = ", ";
        }

        if (string.isEmpty()) {
            string = PriveTalkApplication.getInstance().getString(R.string.not_yet_set);
        }

        return string;
    }

}
