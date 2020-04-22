package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.InterestObject;
import com.privetalk.app.utilities.Links;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by zeniosagapiou on 09/03/16.
 */
public class InterestsDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String collumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static InterestsDatasource instance;

    private InterestsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static InterestsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new InterestsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public ArrayList<InterestObject> getAllInterestsForType(String interestType, String searchFilter) {

        ArrayList<InterestObject> interestObjectArrayList = new ArrayList<>();
        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.InterestTable.TABLE_NAME, collumns,
                        PriveTalkTables.InterestTable.INTEREST_TYPE + " = '" + interestType + "' AND " +
                                PriveTalkTables.InterestTable.TITLE + " LIKE '%" + searchFilter + "%'"
                        , null, null, null, PriveTalkTables.InterestTable.TITLE + " COLLATE NOCASE ASC");
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException e) {
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    interestObjectArrayList.add(new InterestObject(cursor));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        return interestObjectArrayList;
    }

    public ArrayList<InterestObject> getAllInterestsForTypeNotUserAdded(String interestType, String searchFilter) {

        ArrayList<InterestObject> interestObjectArrayList = new ArrayList<>();
        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.InterestTable.TABLE_NAME, collumns,
                        PriveTalkTables.InterestTable.INTEREST_TYPE + " = '" + interestType + "' AND " +
                                PriveTalkTables.InterestTable.USER_ADDED + " = '" + 0 + "' AND " +
                                PriveTalkTables.InterestTable.TITLE + " LIKE '%" + searchFilter + "%'"
                        , null, null, null, PriveTalkTables.InterestTable.TITLE + " COLLATE NOCASE ASC");
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException e) {
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    interestObjectArrayList.add(new InterestObject(cursor));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        return interestObjectArrayList;
    }

    public void saveInterestsOfType(ArrayList<InterestObject> interestObjectArrayList) {

        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourceLock.acquire();
            database.beginTransaction();
            try {

                for (InterestObject interestObject : interestObjectArrayList) {
//                    System.out.println("Adding Interest Many: " + interestObject.title);
                    database.insertWithOnConflict(PriveTalkTables.InterestTable.TABLE_NAME, "", interestObject.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
                }

            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                datasourceLock.release();
                PTSQLiteHelper.databaseLock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void deleteDataOfType(String intentFilter) {

        String where = "";

        switch (intentFilter) {
            case Links.PLACES:
                where = "p";
                break;
            case Links.LITERATURE:
                where = "l";
                break;
            case Links.MOVIES:
                where = "mo";
                break;
            case Links.MUSIC:
                where = "m";
                break;
            case Links.ACTIVITIES:
                where = "a";
                break;
            case Links.OCCUPATION:
                where = "o";
                break;
        }

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.InterestTable.TABLE_NAME, PriveTalkTables.InterestTable.INTEREST_TYPE + " = '" + where + "'", null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


    }

    public boolean isInterestObjectInDatabase(InterestObject interestObject) {
        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.InterestTable.TABLE_NAME, collumns,
                        PriveTalkTables.InterestTable.ID + " = '" + interestObject.id + "'"
                        , null, null, null, null);

            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean value = false;
        if (cursor != null) {
            value = cursor.moveToFirst();
            cursor.close();
        }

        return value;
    }

    public InterestObject getInterestFromDatabase(InterestObject interestObject) {
        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.InterestTable.TABLE_NAME, collumns,
                        PriveTalkTables.InterestTable.ID + " = '" + interestObject.id + "'"
                        , null, null, null, null);

            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InterestObject value = null;
        if (cursor != null) {
            if (cursor.moveToFirst())
                value = new InterestObject(cursor);
            cursor.close();
        }

        return value;
    }

    public void saveInterestInDatabase(InterestObject interestObject) {

//        System.out.println("Adding Interest One: " + interestObject.title);
        try {
            datasourceLock.acquire();
            try {
                database.insertWithOnConflict(PriveTalkTables.InterestTable.TABLE_NAME, "", interestObject.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void deleteAllInterests(){
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.InterestTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
