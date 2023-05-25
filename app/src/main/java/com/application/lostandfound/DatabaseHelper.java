package com.application.lostandfound;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static void init(Context context) {
        instance = new DatabaseHelper(context);
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, "LostAndFound.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE lost_and_found (id INTEGER PRIMARY KEY AUTOINCREMENT, lostOrFound TEXT, name TEXT, number INTEGER, description TEXT, location TEXT, lat REAL, lon REAL, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists lost_and_found");
    }

    public Boolean insertItem(String name, String lostOrFound, String number, String description, String location, double lat, double lon, String date) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("lostOrFound", lostOrFound);
        contentValues.put("number", number);
        contentValues.put("description", description);
        contentValues.put("location", location);
        contentValues.put("lat", lat);
        contentValues.put("lon", lon);
        contentValues.put("date", date);
        long result = DB.insert("lost_and_found", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean deleteItem(int id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        try {
            DB.delete("lost_and_found", "id = ?", new String[]{String.valueOf(id)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("Range")
    public List<Item> getData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from lost_and_found", null);
        List<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setLostOrFound(cursor.getString(cursor.getColumnIndex("lostOrFound")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
            item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            item.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLon(cursor.getDouble(cursor.getColumnIndex("lon")));
            item.setDate(cursor.getString(cursor.getColumnIndex("date")));
            items.add(item);
        }
        return items;
    }

}
