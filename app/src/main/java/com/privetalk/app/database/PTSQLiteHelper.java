package com.privetalk.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.Semaphore;

/**
 * Created by zachariashad on 11/01/16.
 */
public class PTSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PTDatabase";
    private static final int DATABASE_VERSION = 7;

    private static PTSQLiteHelper instance;
    private static SQLiteDatabase myWritableDb;

    public static final Semaphore databaseLock = new Semaphore(1, true);


    public PTSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized PTSQLiteHelper getInstance(Context context) {
        if (instance == null)
            instance = new PTSQLiteHelper(context.getApplicationContext());

        return instance;
    }

    public synchronized SQLiteDatabase getMyWritableDatabase() {
        if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
            myWritableDb = this.getWritableDatabase();
        }

        return myWritableDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PriveTalkTables.CurrentUserTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.CurrentUserDetailsTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.CurrentUserPhotosTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.ETagTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.CurrentUserProfileSettingsTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.CommunityUsersTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.ConfigurationScoresTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.PromotedUsersTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.ProfileVisitorsTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.FavoritesTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.HotMatchesTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.ConversationsTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.MessagesTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.GiftsTables.CREATE_TABLE);
        db.execSQL(PriveTalkTables.TimeStepsTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.InterestTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.CoinsPlansTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.NotificationsTable.CREATE_TABLE);
        db.execSQL(PriveTalkTables.BadgesTable.CREATE_TABLE);
        for (int i = 0; i < PriveTalkTables.AttributesTables.TABLES_NAME.length; i++) {
            db.execSQL(PriveTalkTables.AttributesTables.getCreateTableString(PriveTalkTables.AttributesTables.TABLES_NAME[i]));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//
//        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.MessagesTable.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.HotMatchesTable.TABLE_NAME);
//        db.execSQL(PriveTalkTables.MessagesTable.CREATE_TABLE);
//        db.execSQL(PriveTalkTables.HotMatchesTable.CREATE_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.CurrentUserTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.CurrentUserDetailsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.CurrentUserPhotosTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.ETagTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.CurrentUserProfileSettingsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.CommunityUsersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.ConfigurationScoresTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.PromotedUsersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.ProfileVisitorsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.FavoritesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.HotMatchesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.ConversationsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.GiftsTables.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.MessagesTable.TABLE_NAME);
//        db.execSQL(PriveTalkTables.GiftsTables.CREATE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.TimeStepsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.InterestTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.CoinsPlansTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.NotificationsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.BadgesTable.TABLE_NAME);
        for (int i = 0; i < PriveTalkTables.AttributesTables.TABLES_NAME.length; i++) {
            db.execSQL("DROP TABLE IF EXISTS " + PriveTalkTables.AttributesTables.TABLES_NAME[i]);
        }

        onCreate(db);
    }

}
