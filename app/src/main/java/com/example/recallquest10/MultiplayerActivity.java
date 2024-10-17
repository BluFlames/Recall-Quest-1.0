package com.example.recallquest10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MultiplayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_multiplayer);

        Button hostButton = findViewById(R.id.host_button);
        Button joinButton = findViewById(R.id.join_button);

        hostButton.setOnClickListener(v -> {
            Intent intent = new Intent(MultiplayerActivity.this, HostActivity.class);
            startActivity(intent);
        });

        joinButton.setOnClickListener(v -> {
            Intent intent = new Intent(MultiplayerActivity.this, HostActivity.class);
            startActivity(intent);
        });
    }
}
