package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.InterestsDatasource;
import com.privetalk.app.utilities.Links;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zeniosagapiou on 16/02/16.
 */
public class InterestObject implements Serializable {

    public String id;
    public String title = "";
    public String interest_type = "";
    public boolean user_added = false;

    public InterestObject(JSONObject singleInterestObjectFromServer) {

        if (singleInterestObjectFromServer == null)
            return;

        id = singleInterestObjectFromServer.optString("interest_type") + singleInterestObjectFromServer.optString("title");
        title = singleInterestObjectFromServer.optString("title");
        interest_type = singleInterestObjectFromServer.optString("interest_type");
        user_added = singleInterestObjectFromServer.optBoolean("user_added");
    }

    public InterestObject(Cursor cursor) {

        id = cursor.getString(cursor.getColumnIndex(PriveTalkTables.InterestTable.ID));
        title = cursor.getString(cursor.getColumnIndex(PriveTalkTables.InterestTable.TITLE));
        interest_type = cursor.getString(cursor.getColumnIndex(PriveTalkTables.InterestTable.INTEREST_TYPE));
        user_added = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.InterestTable.USER_ADDED)) == 1;

    }

    public InterestObject(JSONObject jsonObject, String type) {
        if (jsonObject == null)
            return;

        id = type + jsonObject.optString("title");
        title = jsonObject.optString("title");

        interest_type = type;
    }

    public InterestObject(String interest_type) {
        this.interest_type = interest_type;
        this.title = PriveTalkApplication.getInstance().getString(R.string.unspecified);
    }

    public InterestObject(String interestType, String trim) {
        this.id = interestType + trim;
        this.title = trim;
        this.interest_type = interestType;
    }

    public static HashMap<String, ArrayList<InterestObject>> getInterestsListFromServer(JSONArray jsonArray, boolean saveInterest) {

        HashMap<String, ArrayList<InterestObject>> map = new HashMap<>();

        ArrayList<InterestObject> activitiesArrayList = new ArrayList<>();
        ArrayList<InterestObject> musicArrayList = new ArrayList<>();
        ArrayList<InterestObject> moviesArrayList = new ArrayList<>();
        ArrayList<InterestObject> literatureArrayList = new ArrayList<>();
        ArrayList<InterestObject> placesArrayList = new ArrayList<>();
        ArrayList<InterestObject> workArrayList = new ArrayList<>();

        map.put("a", activitiesArrayList);
        map.put("m", musicArrayList);
        map.put("mo", moviesArrayList);
        map.put("l", literatureArrayList);
        map.put("p", placesArrayList);
        map.put("o", workArrayList);

        if (jsonArray == null) {
            return map;
        }

        try {

            for (int i = 0; i < jsonArray.length(); i++) {

                InterestObject interestObject = new InterestObject(jsonArray.getJSONObject(i));

                if (saveInterest) {
                    InterestsDatasource.getInstance(PriveTalkApplication.getInstance()).saveInterestInDatabase(interestObject);
//                    System.out.println("Interest: " + interestObject.title + (interestObject.user_added ? " true" : " false"));
                }

//                Log.d("greekActivities", interestObject.toString());


                switch (interestObject.interest_type) {
                    case "a":
                        activitiesArrayList.add(interestObject);
                        break;
                    case "m":
                        musicArrayList.add(interestObject);
                        break;
                    case "l":
                        literatureArrayList.add(interestObject);
                        break;
                    case "p":
                        placesArrayList.add(interestObject);
                        break;
                    case "mo":
                        moviesArrayList.add(interestObject);
                        break;
                    case "o":
                        workArrayList.add(interestObject);
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static HashMap<String, ArrayList<InterestObject>> getUserInterObjectFromDatabase(String jsonArrayString) {

        HashMap<String, ArrayList<InterestObject>> map = new HashMap<>();

        ArrayList<InterestObject> activitiesArrayList = new ArrayList<>();
        ArrayList<InterestObject> musicArrayList = new ArrayList<>();
        ArrayList<InterestObject> moviesArrayList = new ArrayList<>();
        ArrayList<InterestObject> literatureArrayList = new ArrayList<>();
        ArrayList<InterestObject> placesArrayList = new ArrayList<>();
        ArrayList<InterestObject> workArrayList = new ArrayList<>();

        map.put("a", activitiesArrayList);
        map.put("m", musicArrayList);
        map.put("mo", moviesArrayList);
        map.put("l", literatureArrayList);
        map.put("p", placesArrayList);
        map.put("o", workArrayList);

        try {

            JSONArray jsonArray = new JSONArray(jsonArrayString);

            for (int i = 0; i < jsonArray.length(); i++) {

                InterestObject interestObject = new InterestObject(jsonArray.getJSONObject(i));

                switch (interestObject.interest_type) {
                    case "a":
                        activitiesArrayList.add(interestObject);
                        break;
                    case "m":
                        musicArrayList.add(interestObject);
                        break;
                    case "l":
                        literatureArrayList.add(interestObject);
                        break;
                    case "p":
                        placesArrayList.add(interestObject);
                        break;
                    case "mo":
                        moviesArrayList.add(interestObject);
                        break;
                    case "o":
                        workArrayList.add(interestObject);
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

    public JSONObject getJSONObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("interest_type", interest_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static String toJsonArrayString(HashMap<String, ArrayList<InterestObject>> interestsMap) {

        JSONArray jsonArray = new JSONArray();

        for (ArrayList<InterestObject> interests : interestsMap.values()) {
            for (InterestObject object : interests) {
                if (object.id != null && !object.id.isEmpty() && !object.title.toLowerCase().equals("unspecified"))
                    jsonArray.put(object.getJSONObject());
            }
        }

        return jsonArray.toString();

    }

    public static String getStringFromList(ArrayList<InterestObject> interestObjects) {

        String string = "";
        if (interestObjects == null)
            return string;


        String comma = "";
        for (InterestObject interestObject : interestObjects) {
            string = string + comma + interestObject.title;
            comma = ", ";
        }

        if (string.isEmpty()) {
            string = PriveTalkApplication.getInstance().getString(R.string.not_yet_set);
        }

        return string;
    }

    public static ArrayList<InterestObject> parseInterestsObjectJson(JSONObject jsonObject, String link) {

        Log.d("jsonObject", jsonObject.toString());

        String type = "";
        switch (link) {
            case Links.PLACES:
                type = "p";
                break;
            case Links.LITERATURE:
                type = "l";
                break;
            case Links.MOVIES:
                type = "mo";
                break;
            case Links.MUSIC:
                type = "m";
                break;
            case Links.ACTIVITIES:
                type = "a";
                break;
            case Links.OCCUPATION:
                type = "o";
                break;
        }

        ArrayList<InterestObject> interestObjectArrayList = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                interestObjectArrayList.add(new InterestObject(jsonArray.optJSONObject(i), type));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return interestObjectArrayList;
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.InterestTable.ID, id);
        contentValues.put(PriveTalkTables.InterestTable.INTEREST_TYPE, interest_type);
        contentValues.put(PriveTalkTables.InterestTable.TITLE, title);
        contentValues.put(PriveTalkTables.InterestTable.USER_ADDED, user_added ? 1 : 0);

        return contentValues;
    }

    public synchronized static HashMap<String, ArrayList<InterestObject>> getCommonInterests(HashMap<String, ArrayList<InterestObject>> obj1, HashMap<String, ArrayList<InterestObject>> obj2) {

        HashMap<String, ArrayList<InterestObject>> result = new HashMap<>();

        if (obj1 == null || obj2 == null)
            return result;

        ArrayList<InterestObject> activitiesArrayList = new ArrayList<>();
        ArrayList<InterestObject> musicArrayList = new ArrayList<>();
        ArrayList<InterestObject> moviesArrayList = new ArrayList<>();
        ArrayList<InterestObject> literatureArrayList = new ArrayList<>();
        ArrayList<InterestObject> placesArrayList = new ArrayList<>();
        ArrayList<InterestObject> workArrayList = new ArrayList<>();

        result.put("a", activitiesArrayList);
        result.put("m", musicArrayList);
        result.put("mo", moviesArrayList);
        result.put("l", literatureArrayList);
        result.put("p", placesArrayList);
        result.put("o", workArrayList);

        String key = "";

        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0:
                    key = "a";
                    break;
                case 1:
                    key = "m";
                    break;
                case 2:
                    key = "mo";
                    break;
                case 3:
                    key = "l";
                    break;
                case 4:
                    key = "p";
                    break;
                case 5:
                    key = "o";
                    break;
            }

            for (int j = 0; j < obj1.get(key).size(); j++) {
                for (int k = 0; k < obj2.get(key).size(); k++) {
                    if (obj1.get(key).get(j).title.equals(obj2.get(key).get(k).title))
                        result.get(key).add(obj1.get(key).get(j));
                }
            }

        }
        return result;
    }


    public static int getInterestsCount(HashMap<String, ArrayList<InterestObject>> interests) {
        return interests.get("a").size() +
                interests.get("m").size() +
                interests.get("mo").size() +
                interests.get("l").size() +
                interests.get("o").size() +
                interests.get("p").size();
    }

    @Override
    public String toString() {
        return "id: " + id + ", title: " + title + ", interest_type: " + interest_type;
    }
}
