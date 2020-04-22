package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.HotMatchesObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 26/02/16.
 */
public class HotMatchesDatasource {

    private final Semaphore hotMatchDatasourceLock = new Semaphore(1, true);

    public String ptHMatchAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static HotMatchesDatasource instance;

    private HotMatchesDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static HotMatchesDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new HotMatchesDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public void saveHotMatches(List<HotMatchesObject> hotMatchesObjectList) {
        try {

            PTSQLiteHelper.databaseLock.acquire();

            hotMatchDatasourceLock.acquire();

            try {
                database.beginTransaction();
                for (HotMatchesObject hotMatchesObject : hotMatchesObjectList) {

                    database.insertWithOnConflict(PriveTalkTables.HotMatchesTable.TABLE_NAME, "",
                            hotMatchesObject.getHotMatchObjectContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);
                }
            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                hotMatchDatasourceLock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<HotMatchesObject> getHotMatches() {
        List<HotMatchesObject> hotMatchesObjectList = new LinkedList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.HotMatchesTable.TABLE_NAME, ptHMatchAllColumns, null, null, null, null,
                        PriveTalkTables.HotMatchesTable.ORDER + " ASC");

            } finally {
                PTSQLiteHelper.databaseLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.moveToFirst()) {

            do {
                hotMatchesObjectList.add(new HotMatchesObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return hotMatchesObjectList;
    }


    public void deleteHotMatch(int matchId) {
        try {
            hotMatchDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.HotMatchesTable.TABLE_NAME, PriveTalkTables.HotMatchesTable.MATCH_ID + " = '" + matchId + "'", null);
            } finally {
                hotMatchDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }


    public void deleteHotMatches() {
        try {
            hotMatchDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.HotMatchesTable.TABLE_NAME, null, null);
            } finally {
                hotMatchDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


}
