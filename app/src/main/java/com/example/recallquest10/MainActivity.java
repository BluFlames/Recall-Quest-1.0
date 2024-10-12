package com.example.recallquest10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a1_start_page); // Link to your start page XML file

        // Find the Start button and set a click listener
        Button startButton = findViewById(R.id.start_button);

        // Start button's click event
        startButton.setOnClickListener(v -> {
            // Intent to navigate to AfterStartButton
            Intent intent = new Intent(MainActivity.this, AfterStartButton.class);
            startActivity(intent); // Start AfterStartButton when the Start button is pressed
        });
    }
}
