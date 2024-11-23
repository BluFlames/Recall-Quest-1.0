package com.example.recallquest10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a3_choose_levels); // Link to your gameplay XML file

        Button easyButton = findViewById(R.id.easy_button);
        Button mediumButton = findViewById(R.id.medium_button);
        Button hardButton = findViewById(R.id.hard_button);

        easyButton.setOnClickListener(v -> {
            Intent intent = new Intent(PracticeActivity.this, EasyMode.class);
            startActivity(intent); // Navigate to EasyMode
        });

        mediumButton.setOnClickListener(v -> {
            Intent i = new Intent(PracticeActivity.this, MediumMode.class);
            startActivity(i); // Navigate to MediumMode
        });

        hardButton.setOnClickListener(v -> {
            Intent in = new Intent(PracticeActivity.this, DifficultMode.class);
            startActivity(in); // Navigate to DifficultMode
        });
    }
}
