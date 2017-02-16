package com.example.ash.musicbuddybeta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.SimpleCursorAdapter;

/**
 * Created by Ash on 22-Dec-15.
 */
public class SQLiteDB {

    private static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "device_name";
    public static final String KEY_TIMESTAMP = "time_detail";
    public static final String KEY_TRACK = "track_name";
    public static final String KEY_ARTIST = "artist_name";
    public static final String KEY_TIME = "time_millis";
    public static final String[] ALL_KEYS = {KEY_ROWID, KEY_NAME, KEY_TIMESTAMP, KEY_TRACK, KEY_ARTIST, KEY_TIME};
    public static final String[] SOME_KEYS = {KEY_ROWID, KEY_NAME, KEY_TIMESTAMP, KEY_TRACK, KEY_ARTIST};
    private static final String DATABASE_NAME = "SQLiteDBdb";
    private static final String DATABASE_TABLE = "table_name";
    private static final int DATABASE_VERSION = 1;


    private final Context ourContext;
    private DatabaseHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public SQLiteDB(Context context) {
        ourContext = context;
    }

    public SQLiteDB open() throws SQLException {
        ourHelper = new DatabaseHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        ourHelper.close();
    }

    public void deleteAll() {
        ourDatabase.delete(DATABASE_TABLE, null, null);
    }

    public long insert(int id, String name, String timestamp, String track, String artist, Long time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ROWID, id);
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_TIMESTAMP, timestamp);
        contentValues.put(KEY_TRACK, track);
        contentValues.put(KEY_ARTIST, artist);
        contentValues.put(KEY_TIME, time);
        return ourDatabase.insert(DATABASE_TABLE, null, contentValues);
    }

    public SimpleCursorAdapter getCursorAdapter() {
        /*String[] columns = ALL_KEYS;
        int[] toViewIDs = new int[]{R.id.tvID, R.id.tvName, R.id.tvTimestamp, R.id.tvTrack, R.id.tvArtist, R.id.tvTime};*/
        String[] columns = SOME_KEYS;
        int[] toViewIDs = new int[]{R.id.tvID, R.id.tvName, R.id.tvTimestamp, R.id.tvTrack, R.id.tvArtist};
        Cursor cursor = ourDatabase.query(false, DATABASE_TABLE, columns, null, null,
                null, null, KEY_TIME + " DESC", null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        //return cursor;
        return new SimpleCursorAdapter(ourContext, R.layout.item_layout_new, cursor, columns, toViewIDs, 0);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROWID      + " INTEGER, " +
                    KEY_NAME       + " TEXT, "    +
                    KEY_TIMESTAMP  + " TEXT, "    +
                    KEY_TRACK      + " TEXT, "    +
                    KEY_ARTIST     + " TEXT, "    +
                    KEY_TIME       + " LONG, "    +
                    "PRIMARY KEY (" + KEY_ROWID + "));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP IF EXIST " + DATABASE_NAME);
            onCreate(db);
        }
    }
}
