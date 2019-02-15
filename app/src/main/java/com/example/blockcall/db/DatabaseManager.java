package com.example.blockcall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "blockcall.db";
    public static final String TABLE_BlACKLIST_NAME = "blacklist";
    public static final String TABLE_BLOCKCALL_NAME = "blockcall";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME_CONTACT = "name_contact";
    public static final String COLUMN_PHONE_CONTACT = "phone_contact";
    public static final String COLUMN_DATE_BLOCK = "date_alarm";
    public static final String COLUMN_TIME_BLOCK = "time_alarm";
    public static final int DATABASE_VERSION = 1;



    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tbBlacklist = "CREATE TABLE " + TABLE_BlACKLIST_NAME + "( "
                                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + COLUMN_NAME_CONTACT + " TEXT, "
                                        + COLUMN_PHONE_CONTACT + " TEXT )";

        String tbBlockcall = "CREATE TABLE " + TABLE_BLOCKCALL_NAME + "( "
                                         + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                         + COLUMN_NAME_CONTACT + " TEXT, "
                                         + COLUMN_PHONE_CONTACT + " TEXT, "
                                         + COLUMN_DATE_BLOCK + " TEXT, "
                                         + COLUMN_TIME_BLOCK + " TEXT )";

        sqLiteDatabase.execSQL(tbBlacklist);
        sqLiteDatabase.execSQL(tbBlockcall);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_BlACKLIST_NAME;
        String sql1 = "DROP TABLE IF EXISTS " + TABLE_BLOCKCALL_NAME;
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL(sql1);
        onCreate(sqLiteDatabase);
    }
}
