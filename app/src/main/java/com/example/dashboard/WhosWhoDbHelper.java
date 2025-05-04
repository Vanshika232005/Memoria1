package com.example.dashboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper for the Who's Who game
 * Manages storage and retrieval of people data
 */
public class WhosWhoDbHelper extends SQLiteOpenHelper {

    // Database version and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WhosWhoDb";

    // Table and column names
    private static final String TABLE_PEOPLE = "people";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHOTO_URI = "photo_uri";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_RELATION = "relation";

    // Singleton instance
    private static WhosWhoDbHelper sInstance;

    /**
     * Get singleton instance of the database helper
     */
    public static synchronized WhosWhoDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WhosWhoDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // Private constructor to prevent direct instantiation
    private WhosWhoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PEOPLE_TABLE = "CREATE TABLE " + TABLE_PEOPLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PHOTO_URI + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_RELATION + " TEXT"
                + ")";
        db.execSQL(CREATE_PEOPLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * Add a new person to the database
     */
    public long addPerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, person.getName());
        values.put(KEY_PHOTO_URI, person.getPhotoUri());
        values.put(KEY_CATEGORY, person.getCategory());
        values.put(KEY_RELATION, person.getRelation());

        // Insert row
        long id = db.insert(TABLE_PEOPLE, null, values);
        db.close();
        return id;
    }

    /**
     * Get a person by ID
     */
    public Person getPerson(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PEOPLE,
                new String[]{KEY_ID, KEY_NAME, KEY_PHOTO_URI, KEY_CATEGORY, KEY_RELATION},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Person person = new Person(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return person;
        }
        return null;
    }

    /**
     * Get all people in the database
     */
    public List<Person> getAllPeople() {
        List<Person> peopleList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PEOPLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Person person = new Person(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                peopleList.add(person);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return peopleList;
    }

    /**
     * Get people by category
     */
    public List<Person> getPeopleByCategory(String category) {
        List<Person> peopleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_PEOPLE,
                new String[]{KEY_ID, KEY_NAME, KEY_PHOTO_URI, KEY_CATEGORY, KEY_RELATION},
                KEY_CATEGORY + "=?",
                new String[]{category},
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Person person = new Person(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                peopleList.add(person);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return peopleList;
    }

    /**
     * Update a person's details
     */
    public int updatePerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, person.getName());
        values.put(KEY_PHOTO_URI, person.getPhotoUri());
        values.put(KEY_CATEGORY, person.getCategory());
        values.put(KEY_RELATION, person.getRelation());

        // Update row
        return db.update(TABLE_PEOPLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(person.getId())});
    }

    /**
     * Delete a person
     */
    public void deletePerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PEOPLE, KEY_ID + " = ?",
                new String[]{String.valueOf(person.getId())});
        db.close();
    }

    /**
     * Get count of people
     */
    public int getPeopleCount() {
        String countQuery = "SELECT * FROM " + TABLE_PEOPLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
