package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.CommunityUsersObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 19/02/16.
 */
public class CommunityUsersDatasourse {

    private final Semaphore communityUserDatasourceLock = new Semaphore(1, true);

    public String ptCommunityAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static CommunityUsersDatasourse instance;

    private CommunityUsersDatasourse(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static CommunityUsersDatasourse getInstance(Context context) {

        if (instance == null) {
            instance = new CommunityUsersDatasourse(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public void saveCommunityUsers(List<CommunityUsersObject> communityUsersObjects) {
        try {

            PTSQLiteHelper.databaseLock.acquire();

            communityUserDatasourceLock.acquire();

            try {
                database.beginTransaction();

//                database.delete(PriveTalkTables.CommunityUsersTable.TABLE_NAME, null, null);

                for (CommunityUsersObject communityUsersObject : communityUsersObjects) {

                    database.insertWithOnConflict(PriveTalkTables.CommunityUsersTable.TABLE_NAME, "",
                            communityUsersObject.getCommunityUsersObjectContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);

                }

                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                communityUserDatasourceLock.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public ArrayList<CommunityUsersObject> getCommunityUsers() {
        ArrayList<CommunityUsersObject> communityUsersObjectList = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CommunityUsersTable.TABLE_NAME, ptCommunityAllColumns,
                        null, null, null, null, PriveTalkTables.CommunityUsersTable.ORDER + " ASC");
            } finally {
                PTSQLiteHelper.databaseLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                communityUsersObjectList.add(new CommunityUsersObject(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        return communityUsersObjectList;
    }


    public boolean isEmpty() {
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CommunityUsersTable.TABLE_NAME, ptCommunityAllColumns,
                        null, null, null, null, null);
            } finally {
                PTSQLiteHelper.databaseLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            cursor.close();
        }

        return true;
    }


    public int getCount() {
        int count = 0;
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CommunityUsersTable.TABLE_NAME, ptCommunityAllColumns,
                        null, null, null, null, null);
            } finally {
                PTSQLiteHelper.databaseLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst())
                count = cursor.getCount();

            cursor.close();
        }


        return count;
    }


    public CommunityUsersObject getUserByID(int id) {

        CommunityUsersObject object = new CommunityUsersObject();

        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.CommunityUsersTable.TABLE_NAME, ptCommunityAllColumns, PriveTalkTables.CommunityUsersTable.USER_ID + " = '" + id + "'", null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            object = new CommunityUsersObject(cursor);

            cursor.close();
        }

        return object;
    }


    public void deleteCommunityUsers() {

        try {
            communityUserDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.CommunityUsersTable.TABLE_NAME, null, null);
            } finally {
                communityUserDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }


}
