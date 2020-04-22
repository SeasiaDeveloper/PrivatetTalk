package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.CurrentUserDetails;

import java.util.concurrent.Semaphore;

/**
 * Created by zeniosagapiou on 05/02/16.
 */
public class CurrentUserDetailsDatasource {

    public final Semaphore currentUserDetailsDatasourceLock = new Semaphore(1, true);

    public String allColums[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static CurrentUserDetailsDatasource instance;

    private CurrentUserDetailsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static synchronized CurrentUserDetailsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new CurrentUserDetailsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    //get member details (not a list because only one member can be logged in)
    public CurrentUserDetails getCurrentUserDetails() {

        CurrentUserDetails currentUserDetails = null;
        Cursor cursor = null;

        try {
            currentUserDetailsDatasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserDetailsTable.TABLE_NAME, allColums, null, null, null, null, null);
            } finally {
                currentUserDetailsDatasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currentUserDetails = new CurrentUserDetails(cursor);
            }
            cursor.close();
        }

        return currentUserDetails;
    }

    public void deleteCurrentUserDetails() {

        try {
            currentUserDetailsDatasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.CurrentUserDetailsTable.TABLE_NAME, null, null);
            } finally {
                currentUserDetailsDatasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    //save member details
    public void saveCurrentUserDetails(CurrentUserDetails currentUserDetails) {


        try {
            currentUserDetailsDatasourceLock.acquire();

            try {
                database.insertWithOnConflict(PriveTalkTables.CurrentUserDetailsTable.TABLE_NAME, "", CurrentUserDetails.getContentValues(currentUserDetails), SQLiteDatabase.CONFLICT_REPLACE);

            } finally {
                currentUserDetailsDatasourceLock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}
