package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.GiftObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 04/03/16.
 */
public class GiftsDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String ptGiftsAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static GiftsDatasource instance;

    private GiftsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static GiftsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new GiftsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }


    public void saveGifts(List<GiftObject> giftObjects) {
        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourceLock.acquire();

            try {
                database.beginTransaction();

                for (GiftObject giftObject : giftObjects) {
                    database.insertWithOnConflict(PriveTalkTables.GiftsTables.TABLE_NAME, "",
                            giftObject.getContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);
                }
            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                datasourceLock.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<GiftObject> getGifts() {
        List<GiftObject> giftObjects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.GiftsTables.TABLE_NAME, ptGiftsAllColumns, null, null, null, null,
                    PriveTalkTables.GiftsTables.ORDER + " ASC");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                giftObjects.add(new GiftObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return giftObjects;
    }


    public GiftObject getGiftObjectById(int giftID) {
        GiftObject object = null;
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.GiftsTables.TABLE_NAME, ptGiftsAllColumns,
                    PriveTalkTables.GiftsTables.GIFT_ID + " = '" + giftID + "'", null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            object = new GiftObject(cursor);
            cursor.close();
        }

        return object;
    }


    public void deleteGifts() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.GiftsTables.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

}
