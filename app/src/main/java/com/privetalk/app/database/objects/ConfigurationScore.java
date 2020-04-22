package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.ConfigurationScoreDatasource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zeniosagapiou on 25/02/16.
 */
public class ConfigurationScore {

    public String type;

    public int max;
    public double sort_weight;

    public int benchmark1_value;
    public int benchmark1_purchase_bonus;
    public int benchmark1_coins;
    public int benchmark2_value;
    public int benchmark2_purchase_bonus;
    public int benchmark2_coins;
    public int benchmark3_value;
    public int benchmark3_purchase_bonus;
    public int benchmark3_coins;

    public ConfigurationScore(JSONObject jsonObject) {

        type = jsonObject.optString("type");

        switch (type) {
            case "popular_score":
                max = jsonObject.optInt("popular_max_visits");
                if (max == 0)
                    max = 100;
                sort_weight = jsonObject.optDouble("popular_sort_weight");
                benchmark1_value= jsonObject.optInt("benchmark1_visits");
                benchmark1_purchase_bonus= jsonObject.optInt("benchmark1_purchase_bonus");
                benchmark1_coins= jsonObject.optInt("benchmark1_coins");
                benchmark2_value= jsonObject.optInt("benchmark2_visits");
                benchmark2_purchase_bonus= jsonObject.optInt("benchmark2_purchase_bonus");
                benchmark2_coins= jsonObject.optInt("benchmark2_coins");
                benchmark3_value= jsonObject.optInt("benchmark3_visits");
                benchmark3_purchase_bonus= jsonObject.optInt("benchmark3_purchase_bonus");
                benchmark3_coins= jsonObject.optInt("benchmark3_coins");
                break;
            case "friendly_score":
                max = 100;
                sort_weight = jsonObject.optDouble("benchmark_sort_weight");
                benchmark1_value= jsonObject.optInt("benchmark1_percantage");
                benchmark1_purchase_bonus= jsonObject.optInt("benchmark1_purchase_bonus");
                benchmark1_coins= jsonObject.optInt("benchmark1_coins");
                benchmark2_value= jsonObject.optInt("benchmark2_percantage");
                benchmark2_purchase_bonus= jsonObject.optInt("benchmark2_purchase_bonus");
                benchmark2_coins= jsonObject.optInt("benchmark2_coins");
                benchmark3_value= jsonObject.optInt("benchmark3_percantage");
                benchmark3_purchase_bonus= jsonObject.optInt("benchmark3_purchase_bonus");
                benchmark3_coins= jsonObject.optInt("benchmark3_coins");
                break;
            case "ice_breaker_score":
                max = jsonObject.optInt("ice_breaker_max_chats");
                if (max == 0)
                    max = 100;
                sort_weight = jsonObject.optDouble("ice_breaker_sort_weight");
                benchmark1_value= jsonObject.optInt("benchmark1_chats");
                benchmark1_purchase_bonus= jsonObject.optInt("benchmark1_purchase_bonus");
                benchmark1_coins= jsonObject.optInt("benchmark1_coins");
                benchmark2_value= jsonObject.optInt("benchmark2_chats");
                benchmark2_purchase_bonus= jsonObject.optInt("benchmark2_purchase_bonus");
                benchmark2_coins= jsonObject.optInt("benchmark2_coins");
                benchmark3_value= jsonObject.optInt("benchmark3_chats");
                benchmark3_purchase_bonus= jsonObject.optInt("benchmark3_purchase_bonus");
                benchmark3_coins= jsonObject.optInt("benchmark3_coins");
                break;
            case "impression_voter_score":
                max = jsonObject.optInt("impression_voter_max_votes");
                if (max == 0)
                    max = 100;
                sort_weight = jsonObject.optDouble("impression_sort_weight");
                benchmark1_value= jsonObject.optInt("benchmark1_votes");
                benchmark1_purchase_bonus= jsonObject.optInt("benchmark1_purchase_bonus");
                benchmark1_coins= jsonObject.optInt("benchmark1_coins");
                benchmark2_value= jsonObject.optInt("benchmark2_votes");
                benchmark2_purchase_bonus= jsonObject.optInt("benchmark2_purchase_bonus");
                benchmark2_coins= jsonObject.optInt("benchmark2_coins");
                benchmark3_value= jsonObject.optInt("benchmark3_votes");
                benchmark3_purchase_bonus= jsonObject.optInt("benchmark3_purchase_bonus");
                benchmark3_coins= jsonObject.optInt("benchmark3_coins");
                break;
            case "angel_score":
                max = jsonObject.optInt("angel_positive_impression_max");
                if (max == 0)
                    max = 100;
                sort_weight = jsonObject.optDouble("angel_sort_weight");
                benchmark1_value= jsonObject.optInt("benchmark1_positive_impressions");
                benchmark1_purchase_bonus= jsonObject.optInt("benchmark1_purchase_bonus");
                benchmark1_coins= jsonObject.optInt("benchmark1_coins");
                benchmark2_value= jsonObject.optInt("benchmark2_positive_impressions");
                benchmark2_purchase_bonus= jsonObject.optInt("benchmark2_purchase_bonus");
                benchmark2_coins= jsonObject.optInt("benchmark2_coins");
                benchmark3_value= jsonObject.optInt("benchmark3_positive_impressions");
                benchmark3_purchase_bonus= jsonObject.optInt("benchmark3_purchase_bonus");
                benchmark3_coins= jsonObject.optInt("benchmark3_coins");
                break;
        }
    }

    public ConfigurationScore(Cursor cursor){

          type = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.TYPE));

          max= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.MAX));
          sort_weight= cursor.getDouble(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.SORT_WEIGHT));

          benchmark1_value= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK1_VALUE));
          benchmark1_purchase_bonus= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK1_PURCHASE_BONUS));
          benchmark1_coins= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK1_COINS));
          benchmark2_value= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK2_VALUE));
          benchmark2_purchase_bonus= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK2_PURCHASE_BONUS));
          benchmark2_coins= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK2_COINS));
          benchmark3_value= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK3_VALUE));
          benchmark3_purchase_bonus= cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK3_PURCHASE_BONUS));
          benchmark3_coins = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.ConfigurationScoresTable.BENCHMARK3_COINS));

    }

    public ContentValues getContentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put("type", type);
        contentValues.put("max", max);
        contentValues.put("sort_weight", sort_weight);
        contentValues.put("benchmark1_value", benchmark1_value);
        contentValues.put("benchmark1_purchase_bonus", benchmark1_purchase_bonus);
        contentValues.put("benchmark1_coins", benchmark1_coins);
        contentValues.put("benchmark2_value", benchmark2_value);
        contentValues.put("benchmark2_purchase_bonus", benchmark2_purchase_bonus);
        contentValues.put("benchmark2_coins", benchmark2_coins);
        contentValues.put("benchmark3_value", benchmark3_value);
        contentValues.put("benchmark3_purchase_bonus", benchmark3_purchase_bonus);
        contentValues.put("benchmark3_coins", benchmark3_coins);

        return contentValues;
    }

    public static void parseAndSaveResponse(JSONObject response){

        JSONArray jsonArray = response.optJSONArray("results");

        ArrayList<ConfigurationScore> configurationScoreArrayList = new  ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++){
            configurationScoreArrayList.add(new ConfigurationScore(jsonArray.optJSONObject(i)));
        }


        ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance()).saveConfigurationScores(configurationScoreArrayList);
    }

}
