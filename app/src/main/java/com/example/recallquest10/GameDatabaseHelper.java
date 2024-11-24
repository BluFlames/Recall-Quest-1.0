package com.example.recallquest10;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game_stats.db";
    private static final int DATABASE_VERSION = 2; // Incremented for new changes

    public static final String TABLE_NAME = "game_sessions";
    public static final String COLUMN_SESSION_ID = "session_id"; // Unique session ID for each game
    public static final String COLUMN_MODE = "mode";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";

    // Columns for storing click count for all 16 cards
    public static final String[] CLICK_COLUMNS = {
            "click_card1", "click_card2", "click_card3", "click_card4",
            "click_card5", "click_card6", "click_card7", "click_card8",
            "click_card9", "click_card10", "click_card11", "click_card12",
            "click_card13", "click_card14", "click_card15", "click_card16"
    };

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Construct SQL CREATE TABLE statement
        StringBuilder createTable = new StringBuilder("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MODE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT");

        // Add columns for click counts
        for (String clickColumn : CLICK_COLUMNS) {
            createTable.append(", ").append(clickColumn).append(" INTEGER DEFAULT 0");
        }
        createTable.append(");");

        db.execSQL(createTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
