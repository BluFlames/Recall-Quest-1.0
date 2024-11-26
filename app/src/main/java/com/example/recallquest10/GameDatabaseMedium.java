package com.example.recallquest10;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseMedium extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MediumMode.db";
    private static final int DATABASE_VERSION = 2; // Incremented for new changes

    public static final String TABLE_NAME = "game_sessions";


    public GameDatabaseMedium(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    // Dynamically create a new session table for each game with "card_value" and "click_count" columns
    public void createGameSessionTable(String tableName, SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + tableName + " (" +
                "card_value INTEGER, " +
                "click_count INTEGER DEFAULT 0);";
        db.execSQL(createTableQuery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
