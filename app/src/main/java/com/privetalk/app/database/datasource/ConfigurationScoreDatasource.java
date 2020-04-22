package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.ConfigurationScore;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by zeniosagapiou on 25/02/16.
 */
public class ConfigurationScoreDatasource {

    public static final String POPULAR = "popular_score";
    public static final String FRIENDLY = "friendly_score";
    public static final String ICE_BREAKER = "ice_breaker_score";
    public static final String IMPRESSION = "impression_voter_score";
    public static final String ANGEL = "angel_score";

    private final Semaphore datasourceLock = new Semaphore(1, true);

    public String collumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static ConfigurationScoreDatasource instance;

    private ConfigurationScoreDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static ConfigurationScoreDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new ConfigurationScoreDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }


    //use value for votes/visits/chats/bonus
    //use percent for percentage (friendly only)
    public String getScrorePercentages(String type, float scorePercent, int value) {
        Cursor cursor = null;
        ConfigurationScore configurationScore = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.ConfigurationScoresTable.TABLE_NAME, collumns, PriveTalkTables.ConfigurationScoresTable.TYPE + " = '" + type + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                configurationScore = new ConfigurationScore(cursor);
            }
            cursor.close();
        }

        if (configurationScore == null)
            return " (+" + String.valueOf(0)
                    + "c, +" + String.valueOf(0) + "%)";

        int compareVale = type.equals(FRIENDLY) ? (int) (scorePercent * 100) : value;

//        int scorePercentRound = (int) (scorePercent * 10);

//        System.out.println("Configuration score: " + compareVale + " - " + );

        if (compareVale >= configurationScore.benchmark3_value) {
            return " (+" + String.valueOf(configurationScore.benchmark3_coins)
                    + "c, +" + String.valueOf(configurationScore.benchmark3_purchase_bonus) + "%)";
        } else if (compareVale >= configurationScore.benchmark2_value) {
            return " (+" + String.valueOf(configurationScore.benchmark2_coins)
                    + "c, +" + String.valueOf(configurationScore.benchmark2_purchase_bonus) + "%)";
        } else if (compareVale >= configurationScore.benchmark1_value) {
            return " (+" + String.valueOf(configurationScore.benchmark1_coins)
                    + "c, +" + String.valueOf(configurationScore.benchmark1_purchase_bonus) + "%)";
        } else
            return " (+" + String.valueOf(0) + "c, +" + String.valueOf(0) + "%)";

    }


    public ConfigurationScore getConfigurationScore(String type) {

        Cursor cursor = null;
        ConfigurationScore configurationScore = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.ConfigurationScoresTable.TABLE_NAME, collumns, PriveTalkTables.ConfigurationScoresTable.TYPE + " = '" + type + "'", null, null, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                configurationScore = new ConfigurationScore(cursor);
            }
            cursor.close();
        }

        return configurationScore;
    }

    public void saveConfigurationScores(ArrayList<ConfigurationScore> configurationScores) {

        try {
            datasourceLock.acquire();
            PTSQLiteHelper.databaseLock.acquire();
            database.beginTransaction();
            try {

                for (ConfigurationScore configurationScore : configurationScores) {
                    database.insertWithOnConflict(PriveTalkTables.ConfigurationScoresTable.TABLE_NAME, "", configurationScore.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);

                }

            } finally {
                database.setTransactionSuccessful();
                database.endTransaction();
                datasourceLock.release();
                PTSQLiteHelper.databaseLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }


    public void deleteConfigurations() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.ConfigurationScoresTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

}
