package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.ConversationObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 01/03/16.
 */
public class ConversationsDatasource {
    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String ptConversationsAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static ConversationsDatasource instance;

    private ConversationsDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static ConversationsDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new ConversationsDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public void saveConversations(List<ConversationObject> conversationObjects) {
        try {

            PTSQLiteHelper.databaseLock.acquire();

            datasourceLock.acquire();

            try {
                database.beginTransaction();

                for (ConversationObject conversationObject : conversationObjects) {

                    database.insertWithOnConflict(PriveTalkTables.ConversationsTable.TABLE_NAME, "",
                            conversationObject.getConversationContentValues(),
                            SQLiteDatabase.CONFLICT_REPLACE);
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

    }


    public List<ConversationObject> getConversations() {
        List<ConversationObject> conversationObjects = new ArrayList<>();
        Cursor cursor = null;
        Cursor cursor1 = null;
        Cursor cursor2 = null;
        Cursor cursor3 = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.ConversationsTable.TABLE_NAME, ptConversationsAllColumns,
                    PriveTalkTables.ConversationsTable.IS_ROYAL_USER + " = '" + 1 + "' AND " +
                            PriveTalkTables.ConversationsTable.IS_READ + " = '" + 0 + "'",
                    null, null, null, PriveTalkTables.ConversationsTable.LAST_MSG_TIMESTAMP + " DESC");

            cursor1 = database.query(PriveTalkTables.ConversationsTable.TABLE_NAME, ptConversationsAllColumns,
                    PriveTalkTables.ConversationsTable.IS_ROYAL_USER + " = '" + 0 + "' AND " +
                            PriveTalkTables.ConversationsTable.IS_READ + " = '" + 0 + "'",
                    null, null, null, PriveTalkTables.ConversationsTable.LAST_MSG_TIMESTAMP + " DESC");

            cursor2 = database.query(PriveTalkTables.ConversationsTable.TABLE_NAME, ptConversationsAllColumns,
                    PriveTalkTables.ConversationsTable.IS_ROYAL_USER + " = '" + 1 + "' AND " +
                            PriveTalkTables.ConversationsTable.IS_READ + " = '" + 1 + "'",
                    null, null, null, PriveTalkTables.ConversationsTable.LAST_MSG_TIMESTAMP + " DESC");

            cursor3 = database.query(PriveTalkTables.ConversationsTable.TABLE_NAME, ptConversationsAllColumns,
                    PriveTalkTables.ConversationsTable.IS_ROYAL_USER + " = '" + 0 + "' AND " +
                            PriveTalkTables.ConversationsTable.IS_READ + " = '" + 1 + "'",
                    null, null, null, PriveTalkTables.ConversationsTable.LAST_MSG_TIMESTAMP + " DESC");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    conversationObjects.add(new ConversationObject(cursor));
                } while (cursor.moveToNext());

            cursor.close();
        }

        if (cursor1 != null) {
            if (cursor1.moveToFirst())
                do {
                    conversationObjects.add(new ConversationObject(cursor1));
                } while (cursor1.moveToNext());
            cursor1.close();
        }

        if (cursor2 != null) {
            if (cursor2.moveToFirst())
                do {
                    conversationObjects.add(new ConversationObject(cursor2));
                } while (cursor2.moveToNext());
            cursor2.close();
        }


        if (cursor3 != null) {
            if (cursor3.moveToFirst())
                do {
                    conversationObjects.add(new ConversationObject(cursor3));
                } while (cursor3.moveToNext());
            cursor3.close();
        }

        return conversationObjects;
    }


    public ConversationObject getConversationByUserId(int userID) {
        ConversationObject conversationObject = null;
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.ConversationsTable.TABLE_NAME, ptConversationsAllColumns,
                    PriveTalkTables.ConversationsTable.PARTNER_ID + " = '" + userID + "'", null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            conversationObject = new ConversationObject(cursor);
            cursor.close();
        }

        return conversationObject;
    }

    public List<ConversationObject> searchConversations(String key) {
        List<ConversationObject> objects = new ArrayList<>();

        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.ConversationsTable.TABLE_NAME, ptConversationsAllColumns,
                    PriveTalkTables.ConversationsTable.PARTNER_NAME + " LIKE '%" + key + "%' OR " + PriveTalkTables.ConversationsTable.DESCRIPTION + " LIKE '%" + key + "%'", null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PTSQLiteHelper.getInstance(mContext).databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                objects.add(new ConversationObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return objects;
    }


    public void deleteConversation(int partnerID) {
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.ConversationsTable.TABLE_NAME,
                        PriveTalkTables.ConversationsTable.PARTNER_ID + " = '" + partnerID + "'",
                        null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    public void deleteConversations() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.ConversationsTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
