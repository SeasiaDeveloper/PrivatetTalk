//package net.cocooncreations.privetalk.database.datasource;
//
///**
// * Created by zachariashad on 26/02/16.
// */
//public class HotWheelDatasource {
////    private final Semaphore hotWheelDatasourceLock = new Semaphore(1, true);
////
////    public String ptHWheelAllColumns[] = new String[]{"*"};
////
////    private Context mContext;
////    private SQLiteDatabase database;
////    private static HotWheelDatasource instance;
////
////    private HotWheelDatasource(Context context) {
////        this.mContext = context.getApplicationContext();
////    }
////
////    public static HotWheelDatasource getInstance(Context context) {
////
////        if (instance == null) {
////            instance = new HotWheelDatasource(context);
////        }
////        if (instance.database == null)
////            instance.open();
////
////        return instance;
////    }
////
////    private void open() throws SQLException {
////        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
////    }
////
////    public void saveHotWheelObjects(List<HotWheelUser> hotWheelObjects) {
////        try {
////
////            PTSQLiteHelper.databaseLock.acquire();
////
////            hotWheelDatasourceLock.acquire();
////
////            try {
////                database.beginTransaction();
////
////
////                for (HotWheelUser hotWheelObject : hotWheelObjects) {
////
////                    database.insertWithOnConflict(PriveTalkTables.HotWheelTable.TABLE_NAME, "",
////                            hotWheelObject.getHotWheelObjectContentValues(),
////                            SQLiteDatabase.CONFLICT_REPLACE);
////                }
////
////            } finally {
////                database.setTransactionSuccessful();
////                database.endTransaction();
////                PTSQLiteHelper.databaseLock.release();
////                hotWheelDatasourceLock.release();
////            }
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////    }
////
////
////    public LinkedList<HotWheelUser> getHotWheelObjects() {
////        LinkedList<HotWheelUser> hotWheelObjects = new LinkedList<>();
////        Cursor cursor = null;
////
////        try {
////            PTSQLiteHelper.databaseLock.acquire();
////            try {
////                cursor = database.query(PriveTalkTables.HotWheelTable.TABLE_NAME, ptHWheelAllColumns, null, null, null, null, null);
////
////            } finally {
////                PTSQLiteHelper.databaseLock.release();
////            }
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////
////        if (cursor != null && cursor.moveToFirst()) {
////
////            do {
////                hotWheelObjects.add(new HotWheelUser(cursor));
////            } while (cursor.moveToNext());
////
////            cursor.close();
////        }
////
////        return hotWheelObjects;
////    }
////
////
////    public void deleteHotWheelObjects() {
////
////        try {
////            hotWheelDatasourceLock.acquire();
////            try {
////                database.delete(PriveTalkTables.HotWheelTable.TABLE_NAME, null, null);
////            } finally {
////                hotWheelDatasourceLock.release();
////            }
////        } catch (InterruptedException ex) {
////            ex.printStackTrace();
////        }
////
////    }
//
//}
