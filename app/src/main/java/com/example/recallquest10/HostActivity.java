package com.example.recallquest10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class HostActivity extends AppCompatActivity {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ArrayList<String> connectedPlayers;
    private ArrayAdapter<String> adapter;

    // Static host name (static for all hosts, or generate dynamically)
    private static final String HOST_NAME = "Host1";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1; // Unique request code for location permission

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a9_host); // Replace with your actual host layout

        // Initialize Wi-Fi P2P manager
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        // Set up player list and adapter
        connectedPlayers = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, connectedPlayers);
        ListView playerListView = findViewById(R.id.playerListView);
        playerListView.setAdapter(adapter);

        // Initialize host name display
        TextView hostNameTextView = findViewById(R.id.hostNameTextView);

        // Set static host name
        hostNameTextView.setText("Host: " + HOST_NAME);

        Button startButton = findViewById(R.id.button);
        startButton.setOnClickListener(v -> startGame());

        // Check permissions and start discovering peers
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            discoverPeers(); // Call discoverPeers after permission is granted
        }
    }

    private void discoverPeers() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(HostActivity.this, "Discovering peers...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(HostActivity.this, "Discovery failed: " + reason, Toast.LENGTH_SHORT).show();
            }
        });

        // Request peers once discovery is in progress
        manager.requestPeers(channel, peers -> {
            // Update the player list when new players are discovered
            connectedPlayers.clear(); // Clear existing list
            for (WifiP2pDevice device : peers.getDeviceList()) {
                connectedPlayers.add(device.deviceName); // Add new player device name to list
            }
            adapter.notifyDataSetChanged(); // Notify adapter to update the UI
        });
    }

    private void startGame() {
        if (connectedPlayers.isEmpty()) {
            Toast.makeText(this, "No players connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start the game activity and pass the player list and host name
        Intent intent = new Intent(HostActivity.this, PracticeActivity.class);
        intent.putStringArrayListExtra("playerList", connectedPlayers);
        intent.putExtra("hostName", HOST_NAME);  // Pass static host name to the next activity
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the permission request was for location
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                discoverPeers();  // Now you can proceed with discovering peers
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required to discover peers.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
