package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class CoinsPlan implements Serializable {

    public int id;
    public String name;
    public int coins;
    public int extra_coins;
    public String android_product_id;

    public CoinsPlan(JSONObject jsonObject) {
        id = jsonObject.optInt("id");
        name = jsonObject.optString("name");
        coins = jsonObject.optInt("coins");
        extra_coins = jsonObject.optInt("extra_coins");
        android_product_id = jsonObject.optString("android_product_id");

    }

    public CoinsPlan(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CoinsPlansTable.ID));
        name = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CoinsPlansTable.NAME));
        coins = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CoinsPlansTable.COINS));
        extra_coins = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.CoinsPlansTable.EXTRA_COINS));
        android_product_id = cursor.getString(cursor.getColumnIndex(PriveTalkTables.CoinsPlansTable.ANDROID_PRODUCT_ID));
    }

    public ContentValues getContentValues(){

        ContentValues contentValues = new ContentValues();

        contentValues.put("id", id);
        contentValues.put("name", name);
        contentValues.put("coins", coins);
        contentValues.put("extra_coins", extra_coins);
        contentValues.put("android_product_id", android_product_id);

        return contentValues;
    }

    public static ArrayList<CoinsPlan> parseJson(JSONArray results) {

        ArrayList<CoinsPlan> coinsPlanArrayList = new ArrayList<>();

        for (int i = 0; i < results.length(); i++){
            try {
                coinsPlanArrayList.add(new CoinsPlan(results.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return coinsPlanArrayList;
    }
}