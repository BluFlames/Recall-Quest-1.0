package com.example.recallquest10;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private GameDatabaseEasy dbEasy;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final String TAG = "StatisticsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a8_statistics);

        dbEasy = new GameDatabaseEasy(this);

        // Button to export Easy Mode database
        Button exportEasyModeButton = findViewById(R.id.export_easy_mode_button);
        exportEasyModeButton.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                exportLatestTables(dbEasy.getReadableDatabase()); // Export latest 5 tables
            }
        });
    }

    // Check and request storage permission
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ doesn't need WRITE_EXTERNAL_STORAGE for MediaStore, so we return true
            return true;
        } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            return false;
        }
    }

    private void exportLatestTables(SQLiteDatabase db) {
        List<String> tableNames = getAllTableNames(db);

        // Sort by table name (assumes tables have date/time suffix)
        tableNames.sort(Comparator.reverseOrder()); // Latest first

        // Get the latest `limit` tables
        List<String> latestTables = tableNames.subList(0, Math.min(5, tableNames.size()));

        for (String tableName : latestTables) {
            exportTableToCSV(db, tableName);
        }
    }

    private List<String> getAllTableNames(SQLiteDatabase db) {
        List<String> tableNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (cursor.moveToFirst()) {
            do {
                String tableName = cursor.getString(0);
                tableNames.add(tableName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tableNames;
    }

    // Export database table to CSV in Downloads folder
    private void exportTableToCSV(SQLiteDatabase db, String tableName) {
        String fileName = tableName + "_Export_" + System.currentTimeMillis() + ".txt";
        Uri fileUri;

        // Use MediaStore API for Android 10 and above to save in Downloads
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            fileUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

            if (fileUri == null) {
                Toast.makeText(this, "Error creating file in Downloads.", Toast.LENGTH_LONG).show();
                return;
            }

            try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
                 Writer writer = new OutputStreamWriter(outputStream)) {
                writeCsvDataToOutputStream(db, tableName, writer);
                Toast.makeText(this, "Exported " + tableName + " to Downloads.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error exporting: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error exporting " + tableName, e);
            }

        } else {
            // For Android 9 and below, use external storage directly
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            try (FileWriter fileWriter = new FileWriter(file)) {
                writeCsvDataToOutputStream(db, tableName, fileWriter);
                Toast.makeText(this, "Exported " + tableName + " to Downloads.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error exporting: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error exporting " + tableName, e);
            }
        }
    }

    // Helper method to write data to output stream
    private void writeCsvDataToOutputStream(SQLiteDatabase db, String tableName, Writer writer) throws IOException {
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        Log.d(TAG, "Writing data for table: " + tableName);

        // Check for columns 'card_value' and 'click_count'
        int cardValueIndex = cursor.getColumnIndex("card_value");
        int clickCountIndex = cursor.getColumnIndex("click_count");

        if (cardValueIndex == -1 || clickCountIndex == -1) {
            Toast.makeText(this, "Column names do not match the database table structure.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Column 'card_value' or 'click_count' not found in table " + tableName);
            cursor.close();
            return;
        }

        // Write header row
        writer.append("Card Value, Click Count\n");

        // Write data rows
        while (cursor.moveToNext()) {
            int cardValue = cursor.getInt(cardValueIndex);
            int clickCount = cursor.getInt(clickCountIndex);

            writer.append(cardValue + ", " + clickCount + "\n");
        }
        cursor.close();
        writer.flush();
    }


    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
