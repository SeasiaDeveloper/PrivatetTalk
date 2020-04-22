package com.privetalk.app.database.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.privetalk.app.database.PriveTalkTables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachariashad on 04/03/16.
 */
public class TimeStepsObject {

    public int seconds;
    public float minutes;
    public float hours;
    public float days;

    public TimeStepsObject() {
    }


    public TimeStepsObject(JSONObject obj) {
        this.seconds = obj.optInt("seconds");
        this.minutes = obj.optInt("minutes");
        this.hours = obj.optInt("hours");
        this.days = obj.optInt("days");
    }

    public TimeStepsObject(Cursor cursor) {
        this.seconds = cursor.getInt(cursor.getColumnIndex(PriveTalkTables.TimeStepsTable.SECONDS));
        this.minutes = cursor.getFloat(cursor.getColumnIndex(PriveTalkTables.TimeStepsTable.MINUTES));
        this.hours = cursor.getFloat(cursor.getColumnIndex(PriveTalkTables.TimeStepsTable.HOURS));
        this.days = cursor.getFloat(cursor.getColumnIndex(PriveTalkTables.TimeStepsTable.DAYS));
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PriveTalkTables.TimeStepsTable.SECONDS, this.seconds);
        contentValues.put(PriveTalkTables.TimeStepsTable.MINUTES, this.minutes);
        contentValues.put(PriveTalkTables.TimeStepsTable.HOURS, this.hours);
        contentValues.put(PriveTalkTables.TimeStepsTable.DAYS, this.days);

        return contentValues;
    }


    public static List<TimeStepsObject> timeStepsObjectsParser(JSONArray array) {

        List<TimeStepsObject> objects = new ArrayList<>();

        for (int i = 0; i < array.length(); i++)
            objects.add(new TimeStepsObject(array.optJSONObject(i)));

        return objects;
    }

}
