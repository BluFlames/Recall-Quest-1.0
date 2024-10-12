package com.example.recallquest10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_login_buttons); // This links to your XML layout file containing the buttons

        // Find the buttons in the layout by their IDs
        Button practiceButton = findViewById(R.id.practice_button);
        Button multiplayerButton = findViewById(R.id.multiplayer_button);
        Button statsButton = findViewById(R.id.stats_button);

        // Set onClickListeners to navigate to different activities when the buttons are clicked

        // Practice button's click event
        practiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, PracticeActivity.class);
            startActivity(intent); // Navigate to PracticeActivity
        });

        // Multiplayer button's click event
        multiplayerButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MultiplayerActivity.class);
            startActivity(intent); // Navigate to MultiplayerActivity
        });

        // Statistics button's click event
        statsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, StatisticsActivity.class);
            startActivity(intent); // Navigate to StatisticsActivity
        });
    }
}
