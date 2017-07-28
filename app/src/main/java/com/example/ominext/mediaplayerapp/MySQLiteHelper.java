package com.example.ominext.mediaplayerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ominext on 7/25/2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    //Tên database, tên bảng, version, các cột
    private static final String DATABASE_NAME = "SongDatabase.db";
    private static final String TABLE_NAME = "DetailSong";
    private static final int VERSION = 1;
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_URL = "url";
    private static final String TABLE_CREATE = "create table DetailSong(id integer primary key autoincrement, " +
            "name text, time text, url text)";
    SQLiteDatabase database;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.database = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }

    public int getCount() {
        String query = "select * from " + TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.close();
        return cursor.getCount();
    }
    //lấy ra tất cả các bài hát
    public List<MyData> getAllSong() {
        List<MyData> myDataList = new ArrayList<>();
        String query = "select * from " + TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                MyData myData = new MyData();
                myData.setName(cursor.getString(1));
                myData.setTime(cursor.getString(2));
                myData.setUrl(cursor.getString(3));
//                myData.setLyric(cursor.getString(4));
                myDataList.add(myData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return myDataList;
    }
    //chèn vào các bài hát
    public void insert(MyData data) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, data.getName());
        values.put(COLUMN_TIME, data.getTime());
        values.put(COLUMN_URL, data.getUrl());
//        values.put(COLUMN_LYRIC,data.getLyric());
        database.insert(TABLE_NAME, null, values);
        database.close();
    }
}
