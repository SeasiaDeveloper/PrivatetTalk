package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 12/02/16.
 */
public class AttributesObject implements Serializable {

    public String tableName;
    public String display;
    public String value;

    public AttributesObject(JSONObject attributeObject, String tableName) {

        this.tableName = tableName;
        this.display = attributeObject.optString("display");
        this.value = attributeObject.optString("val");
    }

    public static synchronized void parseAndSaveAttributeArray(JSONArray theAttributesArray, String tableName) {

        ArrayList<AttributesObject> attributesObjectArrayList = new ArrayList<>();

        for (int i = 0; i < theAttributesArray.length(); i++) {
            attributesObjectArrayList.add(new AttributesObject(theAttributesArray.optJSONObject(i), tableName));
        }

        ArrayList<ContentValues> attributeContentValues = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).createAttributeContentValues(attributesObjectArrayList);
        AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).saveAttributeContentValues(attributeContentValues, tableName);
    }

    public AttributesObject(Cursor cursor, String tableName) {
        this.value = cursor.getString(cursor.getColumnIndex(PriveTalkTables.AttributesTables.VALUE));
        this.display = cursor.getString(cursor.getColumnIndex(PriveTalkTables.AttributesTables.DISPLAY));
        this.tableName = tableName;
    }

    public AttributesObject() {
        this.value = "0";
        this.display = PriveTalkApplication.getInstance().getString(R.string.unspecified);
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PriveTalkTables.AttributesTables.VALUE, this.value);
        contentValues.put(PriveTalkTables.AttributesTables.DISPLAY, this.display);

        return contentValues;
    }

    public static AttributesObject getAttributeObject(String value, String tableName) {
        return AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(tableName, value);
    }

    public static AttributesObject getAttributeObject(int value, String tableName) {
        return AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(tableName, String.valueOf(value));
    }

    public static String getStringFromList(ArrayList<AttributesObject> list) {

        String string = "";

        String comma = "";
        for (AttributesObject attributesObject : list) {
            string = string + comma + attributesObject.display;
            comma = ", ";
        }

        if (string.isEmpty()) {
            string = PriveTalkApplication.getInstance().getString(R.string.not_yet_set);
        }

        return string;
    }

//    public static ArrayList<AttributesObject> getAttributeObjectList(JSONObject jsonObject, String responseFieldName){
//
//        ArrayList<AttributesObject> attributesObjectArrayList = new ArrayList<>();
//
//        JSONArray jsonArray = jsonObject.optJSONArray(responseFieldName);
//
//        if (jsonArray != null){
//            for (int i = 0; i < jsonArray.length(); i++){
//                attributesObjectArrayList.add(getAttributeObject(PriveTalkTables.AttributesTables.getTableString(responseFieldName), jsonArray.getJSONObject()));
//            }
//        }
//
//        return attributesObjectArrayList;
//    }


//
//    public static CurrentAttributeObject cursorToAttributeObject(String attributeId) {
//
//        return AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(attributeId);
//
//        int value = jsonObject.optInt(responseFieldName);
//
//        String display = PriveTalkApplication.getInstance().getString(R.string.unspecified);
//
//        try {
//            display = (new JSONArray(attributesObject.jsonArray)).getString(value);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return new CurrentAttributeObject(responseFieldName, String.valueOf(value), display);
//    }

}
