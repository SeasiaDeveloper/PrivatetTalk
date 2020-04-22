package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.CurrentUser;

import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 11/01/16.
 */
public class CurrentUserDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String ptMemberAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static CurrentUserDatasource instance;

    private CurrentUserDatasource(Context context) {
        this.mContext = PriveTalkApplication.getInstance();
    }

    public static CurrentUserDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new CurrentUserDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    //get member details (not a list because only one member can be logged in)
    public CurrentUser getCurrentUserInfo() {

        CurrentUser currentUser = null;
        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserTable.TABLE_NAME, ptMemberAllColumns, null, null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currentUser = new CurrentUser(cursor);
            }
            cursor.close();
        }

        return currentUser;
    }

    public void deleteCurrentUser() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.CurrentUserTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    //save member details
    public void saveCurrentUser(CurrentUser currentUser) {



        try {
            datasourceLock.acquire();

            try {
                database.insertWithOnConflict(PriveTalkTables.CurrentUserTable.TABLE_NAME, "", currentUser.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);

            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}


