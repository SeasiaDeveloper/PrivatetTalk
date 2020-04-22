package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.ProfileVisitorsObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 25/02/16.
 */
public class ProfileVisitorsDatasource {
    private final Semaphore visitorUsersDatasourceLock = new Semaphore(1, true);

    public String ptVisitorAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static ProfileVisitorsDatasource instance;

    private ProfileVisitorsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static ProfileVisitorsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new ProfileVisitorsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public void saveProfileVisitors(List<ProfileVisitorsObject> profileVisitorsObjects) {
        try {

            PTSQLiteHelper.databaseLock.acquire();

            visitorUsersDatasourceLock.acquire();

            try {
                database.beginTransaction();


                for (ProfileVisitorsObject profileVisitorsObject : profileVisitorsObjects) {

                    database.insertWithOnConflict(PriveTalkTables.ProfileVisitorsTable.TABLE_NAME, "",
                            profileVisitorsObject.getProfileVisitorContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);
                }

            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                visitorUsersDatasourceLock.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<ProfileVisitorsObject> getProfileVisitos() {
        List<ProfileVisitorsObject> profileVisitorsObjects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.ProfileVisitorsTable.TABLE_NAME, ptVisitorAllColumns, null, null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                profileVisitorsObjects.add(new ProfileVisitorsObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return profileVisitorsObjects;
    }


    public void deleteProfileVisitors() {

        try {
            visitorUsersDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.ProfileVisitorsTable.TABLE_NAME, null, null);
            } finally {
                visitorUsersDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public List<ProfileVisitorsObject> getTodayVisitors() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<ProfileVisitorsObject> objects = new ArrayList<>();

        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.ProfileVisitorsTable.TABLE_NAME, ptVisitorAllColumns,
                    PriveTalkTables.ProfileVisitorsTable.LAST_VISIT_DATE + " >= '" + calendar.getTimeInMillis() + "'", null, null, null,
                    PriveTalkTables.ProfileVisitorsTable.LAST_VISIT_DATE + " DESC");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                objects.add(new ProfileVisitorsObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }


        return objects;
    }

    public List<ProfileVisitorsObject> getWeekVisitors() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        int week = calendar.get(Calendar.WEEK_OF_YEAR);


        Calendar today = Calendar.getInstance();
        today.setTime(new Date(System.currentTimeMillis()));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        List<ProfileVisitorsObject> objects = new ArrayList<>();

        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.ProfileVisitorsTable.TABLE_NAME, ptVisitorAllColumns,
                    PriveTalkTables.ProfileVisitorsTable.LAST_VISIT_DATE + " < '" + today.getTimeInMillis() + "'",
                    null, null, null,
                    PriveTalkTables.ProfileVisitorsTable.LAST_VISIT_DATE + " DESC");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null) {

            if (cursor.moveToFirst())
                do {
                    objects.add(new ProfileVisitorsObject(cursor));
                } while (cursor.moveToNext());

            cursor.close();
        }


        return objects;
    }

}
