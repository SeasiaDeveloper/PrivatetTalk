//package net.cocooncreations.privetalk.database.datasource;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//
//import net.cocooncreations.privetalk.database.PTSQLiteHelper;
//import net.cocooncreations.privetalk.database.PriveTalkTables;
//import net.cocooncreations.privetalk.database.objects.AttributesObject;
//import net.cocooncreations.privetalk.database.objects.LanguageObject;
//
//import java.util.ArrayList;
//import java.util.concurrent.Semaphore;
//
///**
// * Created by zeniosagapiou on 15/02/16.
// */
//public class LanguageDatasource {
//
//    public final Semaphore datasourcelock = new Semaphore(1, true);
//
//    public String allColumns[] = new String[]{"*"};
//
//    private Context mContext;
//    private SQLiteDatabase database;
//    private static LanguageDatasource instance;
//
//    private LanguageDatasource(Context context) {
//        this.mContext = context.getApplicationContext();
//    }
//
//    public static LanguageDatasource getInstance(Context context) {
//
//        if (instance == null) {
//            instance = new LanguageDatasource(context);
//        }
//        if (instance.database == null)
//            instance.open();
//
//        return instance;
//    }
//
//    private void open() throws SQLException {
//        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
//    }
//
//    //get member details (not a list because only one member can be logged in)
//    public LanguageObject getSpecif(String id) {
//
//        AttributesObject attributesObject = null;
//        Cursor cursor = null;
//
//        try {
//            datasourcelock.acquire();
//            try {
//                cursor = database.query(PriveTalkTables.AttributesTable.TABLE_NAME, allColumns, PriveTalkTables.AttributesTable.ID + " = '" + id + "'", null, null, null, null);
//            } finally {
//                datasourcelock.release();
//            }
//
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                attributesObject = new AttributesObject(cursor);
//            }
//            cursor.close();
//        }
//
//        return attributesObject;
//    }
//
//    public ContentValues createAttributeContentValue(AttributesObject attributesObject) {
//
//        return attributesObject.getContentValues();
//    }
//
//    public ArrayList<ContentValues> createAttributeContentValues(ArrayList<AttributesObject> attributesObjectList) {
//
//        ArrayList<ContentValues> contentValues = new ArrayList<>();
//
//        for (AttributesObject attributesObject : attributesObjectList) {
//            contentValues.add(attributesObject.getContentValues());
//        }
//
//        return contentValues;
//    }
//
//    public void saveAttributeContentValues(ArrayList<ContentValues> contentValuesArrayList) {
//
//        try {
//            PTSQLiteHelper.databaseLock.acquire();
//            datasourcelock.acquire();
//            try {
//                for (ContentValues values : contentValuesArrayList) {
//                    database.insertWithOnConflict(PriveTalkTables.AttributesTable.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
//                }
//            } finally {
//                PTSQLiteHelper.databaseLock.acquire();
//                datasourcelock.release();
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void saveAttributes(AttributesObject attributesObject) {
//
//        try {
//            PTSQLiteHelper.databaseLock.acquire();
//            datasourcelock.acquire();
//
//            try {
//                database.insertWithOnConflict(PriveTalkTables.AttributesTable.TABLE_NAME, "", attributesObject.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
//
//            } finally {
//                PTSQLiteHelper.databaseLock.acquire();
//                datasourcelock.release();
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
