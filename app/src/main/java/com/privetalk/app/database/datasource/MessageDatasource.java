package com.privetalk.app.database.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.MessageObject;
import com.privetalk.app.utilities.PriveTalkUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 02/03/16.
 */
public class MessageDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String ptMessagesAllColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static MessageDatasource instance;

    private MessageDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static MessageDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new MessageDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public void saveMessages(List<MessageObject> messageObjects) {
        try {

            PTSQLiteHelper.databaseLock.acquire();

            datasourceLock.acquire();

            try {
                database.beginTransaction();


                for (MessageObject messageObject : messageObjects) {

                    Cursor cursor = database.query(PriveTalkTables.MessagesTable.TABLE_NAME, ptMessagesAllColumns,
                            PriveTalkTables.MessagesTable.MSG_ID + " = '" + messageObject.messageID + "'"
                                    + " AND ("
                                    + PriveTalkTables.MessagesTable.EXPIRED + " = '" + 1 + "' OR " + PriveTalkTables.MessagesTable.EXPIRES_ON + " != '" + 0 + "')",
                            null, null, null, null);

                    if (cursor == null || !cursor.moveToFirst()) {
                        database.insertWithOnConflict(PriveTalkTables.MessagesTable.TABLE_NAME, "",
                                messageObject.getContentValues(),
                                SQLiteDatabase.CONFLICT_REPLACE);
                    }

                    if (cursor != null)
                        cursor.close();
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


    public List<MessageObject> getMessagesWithUserId(int senderID) {
        List<MessageObject> messageObjects = new ArrayList<>();
        Cursor cursor = null;

        try {
            PTSQLiteHelper.databaseLock.acquire();
            cursor = database.query(PriveTalkTables.MessagesTable.TABLE_NAME, ptMessagesAllColumns,
                    "(" + PriveTalkTables.MessagesTable.SENDER_ID + " = '" + senderID + "' OR "
                            + PriveTalkTables.MessagesTable.RECEIVER_ID + " = '" + senderID + "') AND "
                            + PriveTalkTables.MessagesTable.EXPIRED + " = '" + 0 + "'",
                    null, null, null, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PTSQLiteHelper.databaseLock.release();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                messageObjects.add(new MessageObject(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return messageObjects;
    }


    public void deleteMessagesFromSender(int senderID) {
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.MessagesTable.TABLE_NAME,
                        PriveTalkTables.MessagesTable.SENDER_ID + " = '" + senderID + "' OR " + PriveTalkTables.MessagesTable.RECEIVER_ID + " = '" + senderID + "'",
                        null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void deleteMessages(int userId, int senderID) {
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.MessagesTable.TABLE_NAME,
                        PriveTalkTables.MessagesTable.SENDER_ID + " = '" + userId + "' AND " + PriveTalkTables.MessagesTable.RECEIVER_ID + " = '" + senderID + "'",
                        null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void deleteOldMessages() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.MessagesTable.TABLE_NAME,
                        PriveTalkTables.MessagesTable.EXPIRES_ON + " != '" + 0 + "' AND " + PriveTalkTables.MessagesTable.EXPIRES_ON + " <= '"
                                + (System.currentTimeMillis() - PriveTalkUtilities.getGlobalTimeDifference()) + "'",
                        null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void setExpiredMessages() {

        try {
            datasourceLock.acquire();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PriveTalkTables.MessagesTable.EXPIRED, 1);

                database.update(PriveTalkTables.MessagesTable.TABLE_NAME, contentValues,
                        PriveTalkTables.MessagesTable.EXPIRES_ON + " != '" + 0 + "' AND "
                                + PriveTalkTables.MessagesTable.EXPIRES_ON + " <= '" + (System.currentTimeMillis() - PriveTalkUtilities.getGlobalTimeDifference()) + "'",
                        null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void deleteMessages() {
        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.MessagesTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    public boolean checkIfCanVote(int senderId) {
        Cursor cursor = null;
        Cursor cursor2 = null;
        boolean canVote = false;
        boolean voteCasted = false;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.MessagesTable.TABLE_NAME, ptMessagesAllColumns,
                        PriveTalkTables.MessagesTable.SENDER_ID + " = '" + senderId + "' AND " + PriveTalkTables.MessagesTable.CAN_VOTE + " = '" + 1 + "'",
                        null, null, null, null);


                cursor2 = database.query(PriveTalkTables.MessagesTable.TABLE_NAME, ptMessagesAllColumns,
                        PriveTalkTables.MessagesTable.SENDER_ID + " = '" + senderId + "' AND " + PriveTalkTables.MessagesTable.VOTE_CASTED + " = '" + 1 + "'",
                        null, null, null, null);


            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


        if (cursor2 != null) {
            voteCasted = cursor2.moveToFirst();
            cursor2.close();
        }


        if (cursor != null) {
            canVote = cursor.moveToFirst();
            cursor.close();
        }

        if (voteCasted)
            return false;
        else
            return canVote;
    }

    public int numberOfMessagesSent(int senderID) {

        Cursor cursor = null;
        int countMessages = 0;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.MessagesTable.TABLE_NAME, new String[]{PriveTalkTables.MessagesTable.MSG_ID},
                        PriveTalkTables.MessagesTable.RECEIVER_ID + " = '" + senderID + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getCount();
            }
            cursor.close();
        }

        return countMessages;
    }

    public MessageObject getMessageByMessageId(int msgId) {

        Cursor cursor = null;
        MessageObject messageObjectl = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.MessagesTable.TABLE_NAME, ptMessagesAllColumns,
                        PriveTalkTables.MessagesTable.MSG_ID + " = '" + msgId + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                messageObjectl = new MessageObject(cursor);
            }
            cursor.close();
        }

        return messageObjectl;
    }

    public void setUserVoted(int partnerID) {

        try {
            datasourceLock.acquire();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PriveTalkTables.MessagesTable.CAN_VOTE, 0);

                database.update(PriveTalkTables.MessagesTable.TABLE_NAME, contentValues,
                        PriveTalkTables.MessagesTable.SENDER_ID + " = '" + partnerID + "'",
                        null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
