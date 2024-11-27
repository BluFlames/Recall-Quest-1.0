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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
    private GameDatabaseMedium dbMedium;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final String TAG = "StatisticsActivity";

    private static final String MAIN_FOLDER_NAME = "RecallQuestExports";
    private static final String EASY_MODE_FOLDER_NAME = "EasyModeExports";
    private static final String MEDIUM_MODE_FOLDER_NAME = "MediumModeExports";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a8_statistics);

        dbEasy = new GameDatabaseEasy(this);
        dbMedium = new GameDatabaseMedium(this);

        // Button to export Easy Mode database
        Button exportEasyModeButton = findViewById(R.id.export_easy_mode_button);
        exportEasyModeButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.glow));
            if (checkStoragePermission()) {
                exportLatestTables(dbEasy.getReadableDatabase(), EASY_MODE_FOLDER_NAME, "EasyMode");
            }
        });

        // Button to export Medium Mode database
        Button exportMediumModeButton = findViewById(R.id.export_medium_mode_button);
        exportMediumModeButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_scale));
            if (checkStoragePermission()) {
                exportLatestTables(dbMedium.getReadableDatabase(), MEDIUM_MODE_FOLDER_NAME, "MediumMode");
            }
        });
    }

    // Check and request storage permission
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;  // Android 10+ doesn't require WRITE_EXTERNAL_STORAGE for Downloads
        } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            return false;
        }
    }

    private void exportLatestTables(SQLiteDatabase db, String subfolderName, String mode) {
        List<String> tableNames = getAllTableNames(db);
        tableNames.sort(Comparator.reverseOrder()); // Sort to get latest tables first
        List<String> latestTables = tableNames.subList(0, Math.min(5, tableNames.size()));

        for (String tableName : latestTables) {
            exportTableToCSV(db, tableName, subfolderName, mode);
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

    private void exportTableToCSV(SQLiteDatabase db, String tableName, String subfolderName, String mode) {
        String fileName = mode + "_" + tableName + "_Export_" + System.currentTimeMillis() + ".csv";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileToDownloadsFolder(db, tableName, subfolderName, fileName);
        } else {
            saveFileToLegacyExternalStorage(db, tableName, subfolderName, fileName);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveFileToDownloadsFolder(SQLiteDatabase db, String tableName, String subfolderName, String fileName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + MAIN_FOLDER_NAME + "/" + subfolderName);

        Uri fileUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

        if (fileUri == null) {
            Toast.makeText(this, "Error creating file in Downloads.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "File URI is null; unable to create file.");
            return;
        }

        try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
             Writer writer = new OutputStreamWriter(outputStream)) {
            writeCsvDataToOutputStream(db, tableName, writer);
            Toast.makeText(this, "Exported " + tableName + " to Downloads/" + MAIN_FOLDER_NAME + "/" + subfolderName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error exporting: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error exporting " + tableName, e);
        }
    }

    private void saveFileToLegacyExternalStorage(SQLiteDatabase db, String tableName, String subfolderName, String fileName) {
        File mainFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), MAIN_FOLDER_NAME);
        File subFolder = new File(mainFolder, subfolderName);

        // Create main and subfolders if they don’t exist
        if (!subFolder.exists()) {
            if (subFolder.mkdirs()) {
                Log.d(TAG, "Created folder: " + subFolder.getAbsolutePath());
            } else {
                Log.e(TAG, "Failed to create folder: " + subFolder.getAbsolutePath());
                Toast.makeText(this, "Error creating folder.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        File file = new File(subFolder, fileName);

        try (FileWriter fileWriter = new FileWriter(file)) {
            writeCsvDataToOutputStream(db, tableName, fileWriter);
            Toast.makeText(this, "Exported " + tableName + " to " + subFolder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error exporting: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error exporting " + tableName, e);
        }
    }

    // Helper method to write data to output stream in CSV format
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

        // Write header row for CSV
        writer.append("Card Value,Click Count\n");

        // Write data rows in CSV format
        while (cursor.moveToNext()) {
            int cardValue = cursor.getInt(cardValueIndex);
            int clickCount = cursor.getInt(clickCountIndex);

            writer.append(String.valueOf(cardValue)).append(",").append(String.valueOf(clickCount)).append("\n");
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
