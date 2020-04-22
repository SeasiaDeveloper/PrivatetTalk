package com.privetalk.app.database.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.privetalk.app.database.PTSQLiteHelper;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.objects.CoinsPlan;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by zeniosagapiou on 10/03/16.
 */
public class CoinsPlanDatasource {

    private final Semaphore datasourceLock = new Semaphore(1, true);

    private String allColumns[] = new String[]{"*"};

    private Context mContext;
    private SQLiteDatabase database;
    private static CoinsPlanDatasource instance;

    private CoinsPlanDatasource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static CoinsPlanDatasource getInstance(Context context) {

        if (instance == null) {
            instance = new CoinsPlanDatasource(context);
        }
        if (instance.database == null)
            instance.open();

        return instance;
    }

    private void open() throws SQLException {
        database = PTSQLiteHelper.getInstance(mContext).getMyWritableDatabase();
    }

    public ArrayList<CoinsPlan> getCoinPlans() {

        ArrayList<CoinsPlan> coinsPlanArrayList = new ArrayList<>();

        Cursor cursor = null;

        try {
            datasourceLock.acquire();
            try {
                cursor = database.query(PriveTalkTables.CoinsPlansTable.TABLE_NAME, allColumns, null, null, null, null, null);
            } finally {
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    coinsPlanArrayList.add(new CoinsPlan(cursor));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        return coinsPlanArrayList;
    }


    public void saveCoinPlans(ArrayList<CoinsPlan> coinsPlanArrayList) {

        try {
            PTSQLiteHelper.databaseLock.acquire();
            datasourceLock.acquire();
            database.beginTransaction();

            database.delete(PriveTalkTables.CoinsPlansTable.TABLE_NAME, null, null);

            try {
                for (CoinsPlan coinsPlan : coinsPlanArrayList){
                    database.insertWithOnConflict(PriveTalkTables.CoinsPlansTable.TABLE_NAME, "", coinsPlan.getContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
                }

            } finally {

                database.setTransactionSuccessful();
                database.endTransaction();
                PTSQLiteHelper.databaseLock.release();
                datasourceLock.release();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void deletePlans() {

        try {
            datasourceLock.acquire();
            try {
                database.delete(PriveTalkTables.CoinsPlansTable.TABLE_NAME, null, null);
            } finally {
                datasourceLock.release();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
