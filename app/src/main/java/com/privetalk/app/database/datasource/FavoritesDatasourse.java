package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.FavoritesObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 19/02/16.
 */
public class FavoritesDatasourse {

    private final Semaphore favoritesDatasourceLock = new Semaphore(1, true);

    public String ptFavoritesAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static FavoritesDatasourse instance;

    private FavoritesDatasourse(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static FavoritesDatasourse getInstance(Context context) {

        if (instance == null) {
            instance = new FavoritesDatasourse(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public void saveFavorites(List<FavoritesObject> favoritesObjects) {
        try {

            PTSQLiteHelper.databaseLock.acquire();

            favoritesDatasourceLock.acquire();

            try {
                database.beginTransaction();

//                database.delete(PriveTalkTables.CommunityUsersTable.TABLE_NAME, null, null);

                for (FavoritesObject favoritesObject : favoritesObjects) {

                    database.insertWithOnConflict(PriveTalkTables.FavoritesTable.TABLE_NAME, "",
                            favoritesObject.getFavoritesObjectContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);
                }

            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                favoritesDatasourceLock.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<FavoritesObject> getFavorites() {
        List<FavoritesObject> favoritesObjects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.FavoritesTable.TABLE_NAME, ptFavoritesAllColumns, PriveTalkTables.FavoritesTable.IS_FAVORITE + " = '" + 1 + "'", null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                favoritesObjects.add(new FavoritesObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return favoritesObjects;
    }


    public List<FavoritesObject> getFavoritesBy() {
        List<FavoritesObject> favoritesObjects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.FavoritesTable.TABLE_NAME, ptFavoritesAllColumns, PriveTalkTables.FavoritesTable.IS_FAVORITE + " = '" + 0 + "'", null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                favoritesObjects.add(new FavoritesObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return favoritesObjects;

    }


//    public CommunityUsersObject getUserByID(int id) {
//
//        CommunityUsersObject object = new CommunityUsersObject();
//
//        Cursor cursor = null;
//
//        try {
//            PTSQLiteHelper.databaseLock.acquire();
//            cursor = database.query(PriveTalkTables.CommunityUsersTable.TABLE_NAME, ptFavoritesAllColumns, PriveTalkTables.CommunityUsersTable.MATCH_ID + " = '" + id + "'", null, null, null, null);
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


    public void deleteEverething() {

        try {
            favoritesDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.FavoritesTable.TABLE_NAME, null, null);
            } finally {
                favoritesDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }


    public void deleteFavorites() {

        try {
            favoritesDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.FavoritesTable.TABLE_NAME, PriveTalkTables.FavoritesTable.IS_FAVORITE + " = '" + 1 + "'", null);
            } finally {
                favoritesDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void deleteFavoriteBy() {
        try {
            favoritesDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.FavoritesTable.TABLE_NAME, PriveTalkTables.FavoritesTable.IS_FAVORITE + " = '" + 0 + "'", null);
            } finally {
                favoritesDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


}
