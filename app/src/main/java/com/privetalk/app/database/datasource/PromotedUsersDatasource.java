package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.PromotedUsersObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 24/02/16.
 */
public class PromotedUsersDatasource {

    private final Semaphore promotedUsersDatasourceLock = new Semaphore(1, true);

    public String ptPromotedAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static PromotedUsersDatasource instance;

    private PromotedUsersDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static PromotedUsersDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new PromotedUsersDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public void savePromotedUserts(List<PromotedUsersObject> promotedUsersObjects) {
        try {

            PTSQLiteHelper.databaseLock.acquire();

            promotedUsersDatasourceLock.acquire();

            try {
                database.beginTransaction();


                for (PromotedUsersObject promotedUsersObject : promotedUsersObjects) {

                    database.insertWithOnConflict(PriveTalkTables.PromotedUsersTable.TABLE_NAME, "",
                            promotedUsersObject.getPromotedUsersContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);
                }

            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                promotedUsersDatasourceLock.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<PromotedUsersObject> getPromotedUsers() {
        List<PromotedUsersObject> promotedUsersObjects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.PromotedUsersTable.TABLE_NAME, ptPromotedAllColumns, null, null, null, null,
                    PriveTalkTables.PromotedUsersTable.PROMOTION_DATE + " DESC");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                promotedUsersObjects.add(new PromotedUsersObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return promotedUsersObjects;
    }


//
//    public CommunityUsersObject getUserByID(int id) {
//
//        CommunityUsersObject object = new CommunityUsersObject();
//
//        Cursor cursor = null;
//
//        try {
//            PTSQLiteHelper.databaseLock.acquire();
//            cursor = database.query(PriveTalkTables.CommunityUsersTable.TABLE_NAME, ptPromotedAllColumns, PriveTalkTables.CommunityUsersTable.MATCH_ID + " = '" + id + "'", null, null, null, null);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        PTSQLiteHelper.getInstance(mContext).databaseLock.release();
//
//        if (cursor != null && cursor.moveToFirst()) {
//
//            object = new CommunityUsersObject(cursor);
//
//            cursor.close();
//        }
//
//        return object;
//    }


    public void deletePromotedUsers() {

        try {
            promotedUsersDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.PromotedUsersTable.TABLE_NAME, null, null);
            } finally {
                promotedUsersDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

}
