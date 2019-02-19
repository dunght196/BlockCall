package com.example.blockcall.db.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.blockcall.db.DatabaseManager;
import com.example.blockcall.model.ContactObj;

import java.util.ArrayList;

public class BlockcallData {

    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private static BlockcallData instance = null;


    public BlockcallData(Context context) {
        this.context = context;
        sqLiteOpenHelper = new DatabaseManager(context);
    }

    public static BlockcallData Instance(Context context) {
        if(instance == null) {
            instance = new BlockcallData(context);
        }
        return instance;
    }

    public void add(ContactObj contactObj) {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseManager.COLUMN_NAME_CONTACT, contactObj.getUserName());
        contentValues.put(DatabaseManager.COLUMN_PHONE_CONTACT, contactObj.getPhoneNum());
        contentValues.put(DatabaseManager.COLUMN_DATE_BLOCK, contactObj.getDateBlock());
        contentValues.put(DatabaseManager.COLUMN_TIME_BLOCK, contactObj.getTimeBlock());

        sqLiteDatabase.insert(DatabaseManager.TABLE_BLOCKCALL_NAME, null, contentValues);
        sqLiteDatabase.close();
    }

    public void delete(ContactObj contactObj) {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        sqLiteDatabase.delete(DatabaseManager.TABLE_BLOCKCALL_NAME, DatabaseManager.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contactObj.getId())});
        sqLiteDatabase.close();
    }

    public void deleteAll() {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + DatabaseManager.TABLE_BLOCKCALL_NAME );
        sqLiteDatabase.close();
    }

    public void update(ContactObj contactObj) {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseManager.COLUMN_NAME_CONTACT, contactObj.getUserName());
        contentValues.put(DatabaseManager.COLUMN_PHONE_CONTACT, contactObj.getPhoneNum());
        contentValues.put(DatabaseManager.COLUMN_DATE_BLOCK, contactObj.getDateBlock());
        contentValues.put(DatabaseManager.COLUMN_TIME_BLOCK, contactObj.getTimeBlock());
        sqLiteDatabase.update(DatabaseManager.TABLE_BLOCKCALL_NAME, contentValues, DatabaseManager.COLUMN_ID + " = ?", new String[]{String.valueOf(contactObj.getId())});
        sqLiteDatabase.close();
    }

    public ArrayList<ContactObj> getAllBlockCall() {
        ArrayList<ContactObj> arrBlock = new ArrayList<>();
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseManager.TABLE_BLOCKCALL_NAME, null);

        if(cursor == null) {
            return  null;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ContactObj contactObj = new ContactObj();
            contactObj.setId(cursor.getInt(0));
            contactObj.setUserName(cursor.getString(1));
            contactObj.setPhoneNum(cursor.getString(2));
            contactObj.setDateBlock(cursor.getString(3));
            contactObj.setTimeBlock(cursor.getString(4));
            cursor.moveToNext();
            arrBlock.add(contactObj);
        }

        cursor.close();
        sqLiteDatabase.close();

        return arrBlock;
    }

    public int getLastId() {
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseManager.TABLE_BLOCKCALL_NAME, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }
}
