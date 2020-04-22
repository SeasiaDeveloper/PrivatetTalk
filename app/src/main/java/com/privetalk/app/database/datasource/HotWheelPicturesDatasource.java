//package net.cocooncreations.privetalk.database.datasource;
//
///**
// * Created by zachariashad on 26/02/16.
// */
//public class HotWheelPicturesDatasource {
////    private final Semaphore hotWheelPictureDatasourceLock = new Semaphore(1, true);
////
////    public String ptHWheelPicAllColumns[] = new String[]{"*"};
////
////    private Context mContext;
////    private SQLiteDatabase database;
////    private static HotWheelPicturesDatasource instance;
////
////    private HotWheelPicturesDatasource(Context context) {
////        this.mContext = context.getApplicationContext();
////    }
////
////    public static HotWheelPicturesDatasource getInstance(Context context) {
////
////        if (instance == null) {
////            instance = new HotWheelPicturesDatasource(context);
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
////    public void saveHotWheelPictures(List<HotWheelPicturesObject> hotWheelPicturesObjects) {
////        try {
////
////            PTSQLiteHelper.databaseLock.acquire();
////
////            hotWheelPictureDatasourceLock.acquire();
////
////            try {
////                database.beginTransaction();
////
////
////                for (HotWheelPicturesObject hotWheelPicture : hotWheelPicturesObjects) {
////
////                    database.insertWithOnConflict(PriveTalkTables.HotWheelPicturesTable.TABLE_NAME, "",
////                            hotWheelPicture.getHotWheelPictureContentValue(),
////                            SQLiteDatabase.CONFLICT_REPLACE);
////                }
////
////            } finally {
////                database.setTransactionSuccessful();
////                database.endTransaction();
////                PTSQLiteHelper.databaseLock.release();
////                hotWheelPictureDatasourceLock.release();
////            }
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////    }
////
////
////    public List<HotWheelPicturesObject> getHotWheelPictures(int userID) {
////        List<HotWheelPicturesObject> picturesObjects = new ArrayList<>();
////        Cursor cursor = null;
////
////        try {
////            PTSQLiteHelper.databaseLock.acquire();
////            cursor = database.query(PriveTalkTables.HotWheelPicturesTable.TABLE_NAME, ptHWheelPicAllColumns,
////                    PriveTalkTables.HotWheelPicturesTable.MATCH_ID + " = '" + userID + "'", null, null, null, null);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        PTSQLiteHelper.getInstance(mContext).databaseLock.release();
////
////        if (cursor != null && cursor.moveToFirst()) {
////
////            do {
////                picturesObjects.add(new HotWheelPicturesObject(cursor));
////            } while (cursor.moveToNext());
////
////            cursor.close();
////        }
////
////        return picturesObjects;
////    }
////
////
////    public void deleteAllPics() {
////        try {
////            hotWheelPictureDatasourceLock.acquire();
////            try {
////                database.delete(PriveTalkTables.HotWheelPicturesTable.TABLE_NAME, null, null);
////            } finally {
////                hotWheelPictureDatasourceLock.release();
////            }
////        } catch (InterruptedException ex) {
////            ex.printStackTrace();
////        }
////
////    }
//
//}
