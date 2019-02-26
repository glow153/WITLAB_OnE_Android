package com.nordicsemi.nrfUARTv2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WitLab on 2018-05-04.
 */

public class DatabaseManager {
    private static final String DATABASE_NAME = "UviMeasure.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private Context mContext;

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(QueryRepository.Create.QUERY_CREATE_TABLE_LOGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            onCreate(sqLiteDatabase);
        }
    }

    public DatabaseManager(Context context) {
        this.mContext = context;
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        dbHelper.onCreate(db);
    }

    public void close() {
        db.close();
    }

    public String selectLastLogToString() {
        Cursor cursor = db.rawQuery(QueryRepository.Select.QUERY_SELECT_LASTLOG, null);
        StringBuilder sb = new StringBuilder();
        if (cursor.moveToFirst()) {
            sb.append(cursor.getString(cursor.getColumnIndex(QueryRepository.COL_DATETIME)));
            sb.append(", ");
            sb.append(cursor.getInt(cursor.getColumnIndex(QueryRepository.COL_UVI)));
        }
        return sb.toString();
    }

    public boolean insertLog(String datetime, double uvi) {
        ContentValues values = new ContentValues();
        values.put(QueryRepository.COL_DATETIME, datetime);
        values.put(QueryRepository.COL_UVI, uvi);
        long resultCode = db.insert(QueryRepository.TABLENAME_LOGS, null, values);
        if (resultCode > 0)
            return true;
        else
            return false;
    }

}
