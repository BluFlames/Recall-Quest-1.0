package com.example.recallquest10;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StatisticsActivity extends AppCompatActivity {

    private GameDatabaseEasy dbEasy;
    private GameDatabaseMedium dbMedium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a8_statistics);

        dbEasy = new GameDatabaseEasy(this);
        dbMedium = new GameDatabaseMedium(this);

        // Request storage permissions
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Button to export Easy Mode database
        Button exportEasyModeButton = findViewById(R.id.export_easy_mode_button);
        exportEasyModeButton.setOnClickListener(v -> exportDatabaseToCSV("EasyMode", "E"));

        // Button to export Medium Mode database
        Button exportMediumModeButton = findViewById(R.id.export_medium_mode_button);
        exportMediumModeButton.setOnClickListener(v -> exportDatabaseToCSV("MediumMode", "M"));
    }

    // Export database to CSV in Downloads folder
    private void exportDatabaseToCSV(String databaseName, String mode) {
        SQLiteDatabase db = dbEasy.getReadableDatabase();
        if (databaseName.equals("MediumMode")) {
            db = dbMedium.getReadableDatabase();
        }

        // Define the file name and path
        String fileName = mode + "_Export_" + System.currentTimeMillis() + ".csv";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        try (FileWriter fileWriter = new FileWriter(file)) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + databaseName, null);

            // Write header row
            fileWriter.append("Card Value, Click Count\n");

            // Write data rows
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int cardValue = cursor.getInt(cursor.getColumnIndex("card_value"));
                @SuppressLint("Range") int clickCount = cursor.getInt(cursor.getColumnIndex("click_count"));

                fileWriter.append(String.valueOf(cardValue)).append(", ").append(String.valueOf(clickCount)).append("\n");
            }
            cursor.close();
            Toast.makeText(this, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Error exporting: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
