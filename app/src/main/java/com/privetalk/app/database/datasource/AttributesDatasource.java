package com.privetalk.app.database.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.AttributesObject;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by zeniosagapiou on 12/02/16.
 */
public class AttributesDatasource {

    public final Semaphore datasourcelock = new Semaphore(1, true);
    private static final String UNSPECIFIED = "Unspecofied";

    public String allColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static AttributesDatasource instance;

    private AttributesDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static AttributesDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new AttributesDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    //get member details (not a list because only one member can be logged in)
    public AttributesObject getSpecificAttribute(String tableName, String value) {

        AttributesObject attributesObject = null;
        Cursor cursor = null;

        try {
            datasourcelock.acquire();
            try {
                cursor = database.query(tableName, allColumns, PriveTalkTables.AttributesTables.VALUE + " = '" + value + "'", null, null, null, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                datasourcelock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                attributesObject = new AttributesObject(cursor, tableName);
            }
            cursor.close();
        }

        if (attributesObject == null) {
            attributesObject = new AttributesObject();
            attributesObject.value = value;
        }

        return attributesObject;
    }

    public ContentValues createAttributeContentValue(AttributesObject attributesObject) {

        return attributesObject.getContentValues();
    }

    public ArrayList<ContentValues> createAttributeContentValues(ArrayList<AttributesObject> attributesObjectList) {

        ArrayList<ContentValues> contentValues = new ArrayList<>();

        for (AttributesObject attributesObject : attributesObjectList) {
            contentValues.add(attributesObject.getContentValues());
        }

        return contentValues;
    }

    public void saveAttributeContentValues(ArrayList<ContentValues> contentValuesArrayList, String tableName) {

        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourcelock.acquire();
            try {
                for (ContentValues values : contentValuesArrayList) {
                    database.insertWithOnConflict(tableName, "", values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            } finally {
                PTSQLiteHelper.databaseLock.release();
                datasourcelock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveAttributes(AttributesObject attributesObject, String tableName) {

        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourcelock.acquire();

            try {
                database.insertWithOnConflict(tableName, "", attributesObject.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);

            } finally {
                PTSQLiteHelper.databaseLock.acquire();
                datasourcelock.release();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<AttributesObject> getAttributeObjectList(String table, String filter) {

        ArrayList<AttributesObject> attributesObjectArrayList = new ArrayList<>();

        Cursor cursor = null;

        try {
            datasourcelock.acquire();
            try {
                cursor = database.query(table, allColumns, PriveTalkTables.AttributesTables.DISPLAY + " LIKE '%" + filter + "%'" +
                        " AND " + PriveTalkTables.AttributesTables.VALUE + " != '0'", null, null, null, null);
//                        PriveTalkTables.AttributesTables.DISPLAY + " COLLATE NOCASE ASC");
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                datasourcelock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    attributesObjectArrayList.add(new AttributesObject(cursor, table));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return attributesObjectArrayList;

    }

    public ArrayList<AttributesObject> getAttributeObjectListAlphabetic(String table, String filter) {

        ArrayList<AttributesObject> attributesObjectArrayList = new ArrayList<>();

        Cursor cursor = null;

        try {
            datasourcelock.acquire();
            try {
                cursor = database.query(table, allColumns, PriveTalkTables.AttributesTables.DISPLAY + " LIKE '%" + filter + "%'" +
                        " AND " + PriveTalkTables.AttributesTables.VALUE + " != '0'", null, null, null,
                        PriveTalkTables.AttributesTables.DISPLAY + " COLLATE NOCASE ASC");
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                datasourcelock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    attributesObjectArrayList.add(new AttributesObject(cursor, table));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return attributesObjectArrayList;

    }


    public void deleteAll() {
        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourcelock.acquire();
            try {
                database.beginTransaction();
                for (int i = 0; i < PriveTalkTables.AttributesTables.TABLES_NAME.length; i++)
                    database.delete(PriveTalkTables.AttributesTables.TABLES_NAME[i], null, null);
            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                datasourcelock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
