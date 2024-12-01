package com.example.recallquest10;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Random;

public class JoinActivity extends AppCompatActivity {

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    TextView hostNameTextView;

    private final ArrayList<String> playerList = new ArrayList<>();
    private static final String[] NAMES = {"Hydrogen", "Oxygen", "Carbon", "Nitrogen", "Helium"};

    private static final int REQUEST_CODE_LOCATION = 1;
    private static final int REQUEST_CODE_WIFI_DEVICES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a10_join);

        hostNameTextView = findViewById(R.id.hostNameTextView);
        Button joinButton = findViewById(R.id.joinButton);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        joinButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request location permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                // Request nearby wifi devices permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.NEARBY_WIFI_DEVICES},
                        REQUEST_CODE_WIFI_DEVICES);
            } else {
                // Permissions granted, proceed with connection
                displayHostName();
                connectToHost();
            }
        });
    }

    private void displayHostName() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if (ssid != null && !ssid.equals("<unknown ssid>") && !ssid.equals("unknown ssid")) {
                hostNameTextView.setText("Host: " + ssid.replace("\"", ""));
            } else {
                hostNameTextView.setText("Unknown SSID or private network");
            }
        } else {
            Toast.makeText(this, "Unable to fetch Wi-Fi info.", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToHost() {
        WifiP2pConfig config = new WifiP2pConfig();
        // Here, you can add more settings to configure the connection if necessary, for example:
        // config.deviceAddress = "<host_device_address>";

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(JoinActivity.this, "Connected to host", Toast.LENGTH_SHORT).show();
                String playerName = getRandomName();
                playerList.add(playerName);
                Intent intent = new Intent(JoinActivity.this, WaitingForHostActivity.class);
                intent.putStringArrayListExtra("playerList", playerList);
                startActivity(intent);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(JoinActivity.this, "Connection failed: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRandomName() {
        Random random = new Random();
        int index = random.nextInt(NAMES.length);
        return NAMES[index] + " " + (playerList.size() + 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, now request nearby Wi-Fi devices permission if not granted yet
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.NEARBY_WIFI_DEVICES},
                        REQUEST_CODE_WIFI_DEVICES);
            } else {
                Toast.makeText(this, "Location permission denied. Cannot use Wi-Fi Direct.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_WIFI_DEVICES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with connection
                connectToHost();
            } else {
                Toast.makeText(this, "Nearby Wi-Fi Devices permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
