package com.example.task91p.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.task91p.util.Util;
import com.example.task91p.model.Item;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "("
                + Util.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Util.NAME + " TEXT,"
                + Util.PHONE + " TEXT,"
                + Util.DESCRIPTION + " TEXT,"
                + Util.DATE + " TEXT,"
                + Util.LATITUDE + " REAL,"
                + Util.LONGITUDE + " REAL"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_ITEM_TABLE = "DROP TABLE IF EXISTS " + Util.TABLE_NAME;
        db.execSQL(DROP_ITEM_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Insert new item into database (Update-type function)
    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.NAME, item.getName());
        values.put(Util.PHONE, item.getPhone());
        values.put(Util.DESCRIPTION, item.getDescription());
        values.put(Util.DATE, item.getDate());
        values.put(Util.LATITUDE, item.getLocation().latitude);
        values.put(Util.LONGITUDE, item.getLocation().longitude);
        long newRowId = db.insert(Util.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    // Fetch an item from database using its name (Read-type function)
    public Item fetchItem(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Util.TABLE_NAME,
                null, // Passing null for columns retrieves all columns
                Util.NAME + "=?",
                new String[]{name},
                null, null, null
        );

        Item item = null;
        // Move the cursor to the first match of an item with this name
        if (cursor.moveToFirst()) {
            // Retrieve latitude and longitude from cursor
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Util.LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(Util.LONGITUDE));

            // Create a new Item object to initialise from the fetched data
            item = new Item(
                    cursor.getString(cursor.getColumnIndexOrThrow(Util.NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Util.PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Util.DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Util.DATE)),
                    new LatLng(latitude, longitude)
            );
        }

        cursor.close();
        db.close();
        return item;
    }

    // Fetch all item names from database (Read-type function)
    public String[] fetchItemNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Util.TABLE_NAME,
                new String[]{Util.NAME},
                null, null, null, null, null
        );

        int size = cursor.getCount();
        String[] names = new String[size];
        cursor.moveToFirst();

        for (int i = 0; i < size; i++) {
            // Populate the names string with each item's name from the database
            names[i] = cursor.getString(cursor.getColumnIndexOrThrow(Util.NAME));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return names;
    }

    // Fetch all items from database (as a list of Item objects)
    public List<Item> fetchItems() {
        List<Item> items = new ArrayList<>();
        String[] names = fetchItemNames();

        for (String name : names) {
            // Fetch each item by name and add it to the list
            Item item = fetchItem(name);
            if (item != null) {
                items.add(item);
            }
        }

        return items;
    }

    // Delete an item from database using its name (Delete-type function)
    public boolean deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected =  db.delete(
                Util.TABLE_NAME,
                Util.NAME + "=?",
                new String[]{name}
        );

        return (rowsAffected > 0);
    }
}
