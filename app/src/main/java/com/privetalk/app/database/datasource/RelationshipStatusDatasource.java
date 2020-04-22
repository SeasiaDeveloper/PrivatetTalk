//package net.cocooncreations.privetalk.database.datasource;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.inputmethodservice.KeyboardView;
//
//import net.cocooncreations.privetalk.database.PTSQLiteHelper;
//import net.cocooncreations.privetalk.database.PriveTalkTables;
//import net.cocooncreations.privetalk.database.objects.RelationShipStatus;
//
//import java.util.ArrayList;
//
///**
// * Created by zeniosagapiou on 22/01/16.
// */
//public class RelationshipStatusDatasource {
//
//    private Context mContext;
//    private SQLiteDatabase database;
//    private static RelationshipStatusDatasource instance;
//
//    private RelationshipStatusDatasource(Context context) {
//        this.mContext = context.getApplicationContext();
//    }
//
//    public static RelationshipStatusDatasource getInstance(Context context) {
//
//        if (instance == null) {
//            instance = new RelationshipStatusDatasource(context);
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
//    public synchronized void saveRelationshipStatuses(ArrayList<ContentValues> contentValuesArrayList) {
//
//        try{
//            PTSQLiteHelper.databaseLock.acquire();
//
//            try{
//                database.beginTransaction();
//
//                for (ContentValues values : contentValuesArrayList) {
//                    database.insertWithOnConflict(PriveTalkTables.RelationShipStatusTable.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_REPLACE);
//                }
//                database.setTransactionSuccessful();
//                database.endTransaction();
//            }
//            finally {
//                PTSQLiteHelper.databaseLock.release();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        KeyboardView
//
//
//    }
//
//    public synchronized ArrayList<RelationShipStatus> getRelationShipStatuses(){
//
//        ArrayList<RelationShipStatus> relationShipStatusArrayList = new ArrayList<>();
//
//        Cursor cursor = database.query(PriveTalkTables.RelationShipStatusTable.TABLE_NAME, new String[]{"*"}, null, null, null, null, null);
//
//        if (cursor != null){
//
//            if (cursor.moveToFirst()){
//                do{
//                    relationShipStatusArrayList.add(new RelationShipStatus(cursor));
//                }while(cursor.moveToNext());
//            }
//
//            cursor.close();
//        }
//
//        return relationShipStatusArrayList;
//    };
//
//
//}
