package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.TimeStepsObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 04/03/16.
 */
public class TimeStepsDatasource {


    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String ptTimeStepsAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static TimeStepsDatasource instance;

    private TimeStepsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static TimeStepsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new TimeStepsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }


    public void saveTimeSteps(List<TimeStepsObject> timeStepsObjects) {
        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourceLock.acquire();

            try {
                database.beginTransaction();

                for (TimeStepsObject object : timeStepsObjects) {
                    database.insertWithOnConflict(PriveTalkTables.TimeStepsTable.TABLE_NAME, "",
                            object.getContentValues(),
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


    public List<TimeStepsObject> getTimeSteps() {
        List<TimeStepsObject> objects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.TimeStepsTable.TABLE_NAME, ptTimeStepsAllColumns, null, null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                objects.add(new TimeStepsObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return objects;
    }



    public void deleteTimeSteps() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.TimeStepsTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
