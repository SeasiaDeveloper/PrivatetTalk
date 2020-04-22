package com.privetalk.app.database.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.EtagObject;

import java.util.concurrent.Semaphore;

/**
 * Created by zeniosagapiou on 18/02/16.
 */
public class ETagDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    private String allColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static ETagDatasource instance;

    private ETagDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static ETagDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new ETagDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public EtagObject getEΤagForLink(String link){

        EtagObject etagObject = null;

        Cursor cursor = null;
        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.ETagTable.TABLE_NAME, allColumns, PriveTalkTables.ETagTable.LINK + " = '" + link + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                etagObject = new EtagObject(cursor);
            }
            cursor.close();
        }

        return etagObject;
    }

    public void saveETagForLink(EtagObject etagObject){

        ContentValues contentValues = new ContentValues();
        contentValues.put(PriveTalkTables.ETagTable.ETAG, etagObject.etag);
        contentValues.put(PriveTalkTables.ETagTable.LINK, etagObject.link);

        try {
            datasourceLock.acquire();
            try {
                database.insertWithOnConflict(PriveTalkTables.ETagTable.TABLE_NAME, "", contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void deleteEtags() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.ETagTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

}
