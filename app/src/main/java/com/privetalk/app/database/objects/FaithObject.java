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
public class FaithObject implements Serializable{

    public static final String JSONKEY_RELIGION = "religion";
    public static final String JSON_FAITH_LEVEL= "level";


    public AttributesObject religion= new AttributesObject();
    public AttributesObject faithLevel= new AttributesObject();

    public FaithObject(){

    }

    public FaithObject(JSONObject singleFaithObjectFromServer) {

        if (singleFaithObjectFromServer == null){
            religion = new AttributesObject();
            faithLevel = new AttributesObject();
        }
        else{
            religion = AttributesObject.getAttributeObject(singleFaithObjectFromServer.optString("religion"), PriveTalkTables.AttributesTables.getTableString("faith"));
            faithLevel = AttributesObject.getAttributeObject(singleFaithObjectFromServer.optString("level"), PriveTalkTables.AttributesTables.getTableString("faith_level"));
        }
    }


    public static ArrayList<FaithObject> getFaithListFromServer(JSONArray jsonArray) {

        ArrayList<FaithObject> faithObjectArrayList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                faithObjectArrayList.add(new FaithObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return faithObjectArrayList;
    }


    public JSONObject getJSONObject (){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSONKEY_RELIGION, religion.value);
            jsonObject.put(JSON_FAITH_LEVEL, faithLevel.value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static ArrayList<FaithObject> getUserFaithsFromDatabase(String jsonArrayString){

        ArrayList<FaithObject> faithObjectArrayList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);

            for (int i = 0; i < jsonArray.length(); i++){
                faithObjectArrayList.add(new FaithObject(jsonArray.getJSONObject(i)));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return faithObjectArrayList;

    }

    public static String toJsonArrayString(ArrayList<FaithObject> faithObjects) {

        JSONArray jsonArray = new JSONArray();

        for (FaithObject object : faithObjects){
            jsonArray.put(object.getJSONObject());
        }

        return jsonArray.toString();
    }

    public static String getStringFromList(ArrayList<FaithObject> list){

        String string = "";

        String comma = "";
        for (FaithObject faithObject : list){
            string = string + comma + faithObject.religion.display;
            comma = ", ";
        }

        if (string.isEmpty()){
            string = PriveTalkApplication.getInstance().getString(R.string.not_yet_set);
        }

        return string;
    }

    public static FaithObject getFaithFromDatabase(String jsonString) {

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            return new FaithObject(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        return new FaithObject(new JSONObject());
    }
}
