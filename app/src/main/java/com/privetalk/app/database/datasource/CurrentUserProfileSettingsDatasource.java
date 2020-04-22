package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.ProfileSettings;

import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 18/02/16.
 */
public class CurrentUserProfileSettingsDatasource {


    public final Semaphore currentUserProfileSettingsLock = new Semaphore(1, true);

    public String allColums[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static CurrentUserProfileSettingsDatasource instance;

    private CurrentUserProfileSettingsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static CurrentUserProfileSettingsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new CurrentUserProfileSettingsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }


    public void saveCurrentUserProfileSettings(ProfileSettings profileSettings) {


        try {
            currentUserProfileSettingsLock.acquire();

            try {

                database.insertWithOnConflict(PriveTalkTables.CurrentUserProfileSettingsTable.TABLE_NAME, "",
                        ProfileSettings.getProfileSettingsContentValues(profileSettings), SQLiteDatabase.CONFLICT_REPLACE);

            } finally {
                currentUserProfileSettingsLock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public ProfileSettings getCurrentUserProfileSettings() {

        ProfileSettings profileSettings = null;
        Cursor cursor = null;

        try {
            currentUserProfileSettingsLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CurrentUserProfileSettingsTable.TABLE_NAME, allColums, null, null, null, null, null);
            } finally {
                currentUserProfileSettingsLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                profileSettings = new ProfileSettings(cursor);
            }
            cursor.close();
        }

        return profileSettings;
    }


    public void deleteCurrentUserProfileSettings() {

        try {
            currentUserProfileSettingsLock.acquire();
            try {
                database.delete(PriveTalkTables.CurrentUserProfileSettingsTable.TABLE_NAME, null, null);
            } finally {
                currentUserProfileSettingsLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

}
