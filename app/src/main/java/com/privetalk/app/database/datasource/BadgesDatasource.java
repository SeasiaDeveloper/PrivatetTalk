package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.BadgesObject;

import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 21/04/16.
 */
public class BadgesDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    private String allColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static BadgesDatasource instance;

    private BadgesDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static BadgesDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new BadgesDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }


    public BadgesObject getBadges() {
        BadgesObject object = null;
        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.BadgesTable.TABLE_NAME, allColumns, null, null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    object = new BadgesObject(cursor);
                } while (cursor.moveToNext());

            }
            cursor.close();
        }


        return object == null ? new BadgesObject() : object;
    }


    public void saveBadges(BadgesObject object) {
        try {
            PTSQLiteHelper.databaseLock.acquire();
            database.delete(PriveTalkTables.BadgesTable.TABLE_NAME, null, null);

            try {
                datasourceLock.acquire();
                database.insertWithOnConflict(PriveTalkTables.BadgesTable.TABLE_NAME, "", object.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);

            } finally {
                datasourceLock.release();
                PTSQLiteHelper.databaseLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }



    public void deleteBadges() {
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.BadgesTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
