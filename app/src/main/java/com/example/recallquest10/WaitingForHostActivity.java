package com.example.recallquest10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class WaitingForHostActivity extends AppCompatActivity {

    ListView playersListView;  // ListView to display the list of players

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_list);  // Set the layout you provided

        playersListView = findViewById(R.id.playersListView);  // Find the ListView in your layout

        // Get the player list passed from JoinActivity
        Intent intent = getIntent();
        ArrayList<String> playerList = intent.getStringArrayListExtra("playerList");

        // If the player list is not null and has data, display it in the ListView
        if (playerList != null && !playerList.isEmpty()) {
            // Create an ArrayAdapter to display the list of players
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, playerList);  // Use a simple list item layout
            playersListView.setAdapter(adapter);  // Set the adapter to the ListView
        }
    }
}
