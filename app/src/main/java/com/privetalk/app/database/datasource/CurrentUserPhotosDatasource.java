package com.privetalk.app.database.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.CurrentUserPhotoObject;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by zeniosagapiou on 18/02/16.
 */
public class CurrentUserPhotosDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    private String allColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static CurrentUserPhotosDatasource instance;

    private CurrentUserPhotosDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static CurrentUserPhotosDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new CurrentUserPhotosDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }


    public ArrayList<CurrentUserPhotoObject> getCurrentUserPhotos() {

        ArrayList<CurrentUserPhotoObject> currentUserPhotoObjectArrayList = new ArrayList<>();

        Cursor cursor = null;
        Cursor cursor1 = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, allColumns,
                        /*PriveTalkTables.CurrentUserPhotosTable.IS_MAIN_PROFILE_PHOTO + " = '" + 0 + "'"*/null, null, null, null,
                        PriveTalkTables.CurrentUserPhotosTable.ID + " DESC");
                //   cursor1 = database.query(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, allColumns,PriveTalkTables.CurrentUserPhotosTable.IS_MAIN_PROFILE_PHOTO + " = '" + 1 + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

     /*   if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                do {
                    currentUserPhotoObjectArrayList.add(new CurrentUserPhotoObject(cursor1));
                } while (cursor1.moveToNext());

            }
            cursor1.close();
        }*/

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    currentUserPhotoObjectArrayList.add(new CurrentUserPhotoObject(cursor));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        return currentUserPhotoObjectArrayList;
    }


    public ArrayList<CurrentUserPhotoObject> getCurrentUserPhotosWithoutSorting() {

        ArrayList<CurrentUserPhotoObject> currentUserPhotoObjectArrayList = new ArrayList<>();

        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, allColumns, null, null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    currentUserPhotoObjectArrayList.add(new CurrentUserPhotoObject(cursor));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        return currentUserPhotoObjectArrayList;

    }

    public ArrayList<ContentValues> createCurrentUserPhotosContentValues(ArrayList<CurrentUserPhotoObject> currentUserPhotoObjects) {

        ArrayList<ContentValues> contentValues = new ArrayList<>();

        for (CurrentUserPhotoObject currentUserPhotoObject : currentUserPhotoObjects) {
            contentValues.add(currentUserPhotoObject.getContentValues());
        }

        return contentValues;
    }

    public boolean hasVerfiedPhoto() {
        boolean hasVerifiedPic = false;
        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME,
                        allColumns, PriveTalkTables.CurrentUserPhotosTable.VERIFIED_PHOTO + " = '" + 1 + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            hasVerifiedPic = cursor.moveToFirst();
            cursor.close();
        }

        return hasVerifiedPic;
    }

    public void saveCurrentUserPhotoContentValues(ArrayList<ContentValues> contentValuesArrayList) {

        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourceLock.acquire();
            try {
                for (ContentValues values : contentValuesArrayList) {
                    database.insertWithOnConflict(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            } finally {
                PTSQLiteHelper.databaseLock.release();
                datasourceLock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void savePhoto(CurrentUserPhotoObject currentUserPhotoObject) {

        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourceLock.acquire();

            try {
                database.insertWithOnConflict(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, "", currentUserPhotoObject.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);

            } finally {
                PTSQLiteHelper.databaseLock.release();
                datasourceLock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void deletePhotos() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public CurrentUserPhotoObject getPhotoById(int photoId) {

        CurrentUserPhotoObject currentUserPhotoObject = null;

        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, allColumns, PriveTalkTables.CurrentUserPhotosTable.ID + " = '" + photoId + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currentUserPhotoObject = new CurrentUserPhotoObject(cursor);
            }
            cursor.close();
        }

        return currentUserPhotoObject;
    }

    public CurrentUserPhotoObject getProfilePhoto() {

        CurrentUserPhotoObject currentUserPhotoObject = null;

        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, allColumns, PriveTalkTables.CurrentUserPhotosTable.IS_MAIN_PROFILE_PHOTO + " > '0'" /*+ " AND " + PriveTalkTables.CurrentUserPhotosTable.VERIFIED_PHOTO + " > '0'"*/, null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currentUserPhotoObject = new CurrentUserPhotoObject(cursor);
            }
            cursor.close();
        }

        return currentUserPhotoObject;
    }

    public CurrentUserPhotoObject checkProfilePic(Context context) {
        if (CurrentUserPhotosDatasource.getInstance(context).getProfilePhoto() != null && CurrentUserPhotosDatasource.getInstance(context).hasVerfiedPhoto()) {
            return CurrentUserPhotosDatasource.getInstance(context).getProfilePhoto();
        } else if (getCurrentUserPhotos().size() > 0) {
            return getCurrentUserPhotos().get(getCurrentUserPhotos().size() - 1);
        } else {
            return null;
        }
    }

    public void deletePhoto(CurrentUserPhotoObject currentUserPhotoObject1) {
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME, PriveTalkTables.CurrentUserPhotosTable.ID + " = '" + currentUserPhotoObject1.id + "'", null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
