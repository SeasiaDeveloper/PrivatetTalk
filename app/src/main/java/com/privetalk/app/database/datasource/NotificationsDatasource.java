package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.NotificationObject;
import com.privetalk.app.database.objects.TempNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 15/03/16.
 */
public class NotificationsDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String ptNotificationsAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static NotificationsDatasource instance;

    private NotificationsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static NotificationsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new NotificationsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }


    public void saveNotifications(TempNotification tempNotification) {
        Cursor cursor = null;
        String args = TextUtils.join(", ", tempNotification.ids);


        try {
            PTSQLiteHelper.databaseLock.acquire();

            datasourceLock.acquire();

//            database.execSQL(String.format("DELETE FROM " + PriveTalkTables.NotificationsTable.TABLE_NAME + " WHERE " +
//                    PriveTalkTables.NotificationsTable.ID + " NOT IN (%s);", args));

            try {
                database.beginTransaction();

                for (NotificationObject object : tempNotification.objects) {
                    cursor = database.query(PriveTalkTables.NotificationsTable.TABLE_NAME, ptNotificationsAllColumns,
                            PriveTalkTables.NotificationsTable.ID + " = '" + object.id + "'", null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
//                        System.out.println("Updatind this id: " + cursor.getInt(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.ID)));

                        database.update(PriveTalkTables.NotificationsTable.TABLE_NAME,
                                object.getContentValuesForUpdate(cursor.getInt(cursor.getColumnIndex(PriveTalkTables.NotificationsTable.NOTIFICATION_READ))),
                                PriveTalkTables.NotificationsTable.ID + " = '" + object.id + "'", null);
                    } else {
                        database.insert(PriveTalkTables.NotificationsTable.TABLE_NAME, null, object.getContentValues());
//                        System.out.println("This id not incluted");
                    }
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

        if (cursor != null)
            cursor.close();

    }

    public void markNotificationAsRead(NotificationObject object) {
        try {
            PTSQLiteHelper.databaseLock.acquire();

            datasourceLock.acquire();

            try {

                database.insertWithOnConflict(PriveTalkTables.NotificationsTable.TABLE_NAME, PriveTalkTables.NotificationsTable.ID,
                        object.getContentValues(),
                        SQLiteDatabase.CONFLICT_REPLACE);

            } finally {
                PTSQLiteHelper.databaseLock.release();
                datasourceLock.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getUnreadNotificationsCount() {
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.NotificationsTable.TABLE_NAME, ptNotificationsAllColumns,
                    PriveTalkTables.NotificationsTable.NOTIFICATION_READ + " = '" + 0 + "'", null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int count = cursor.getCount();
                cursor.close();
                return count;
            }
        }

        return 0;
    }


    public List<NotificationObject> getAllNotifications() {
        List<NotificationObject> objects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.NotificationsTable.TABLE_NAME, ptNotificationsAllColumns,
                    null, null, null, null,
                    PriveTalkTables.NotificationsTable.CREATED_ON + " DESC");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                objects.add(new NotificationObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return objects;
    }

//
//    public void deleteMessagesFromSender(int senderID) {
//        try {
//            datasourceLock.acquire();
//            try {
//                database.delete(PriveTalkTables.MessagesTable.TABLE_NAME,
//                        PriveTalkTables.MessagesTable.SENDER_ID + " = '" + senderID + "' OR " + PriveTalkTables.MessagesTable.RECEIVER_ID + " = '" + senderID + "'",
//                        null);
//            } finally {
//                datasourceLock.release();
//            }
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//
//    }
//

    public void deleteAllNotifications() {
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.NotificationsTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    public void saveNotificationsList(List<NotificationObject> notificationObjects) {

        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourceLock.acquire();

            try {
                for (NotificationObject notificationObject : notificationObjects) {
                    database.insertWithOnConflict(PriveTalkTables.NotificationsTable.TABLE_NAME, PriveTalkTables.NotificationsTable.ID,
                            notificationObject.getContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);
                }

            } finally {
                PTSQLiteHelper.databaseLock.release();
                datasourceLock.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
