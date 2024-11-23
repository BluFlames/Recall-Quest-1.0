package com.example.recallquest10;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class HostActivity extends AppCompatActivity {

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a9_host);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (manager != null) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    manager.requestPeers(channel, peerListListener);
                }
            }
        }
    };

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            List<WifiP2pDevice> peers = new ArrayList<>(peerList.getDeviceList());
            // Handle the list of available peers here
        }
    };

    WifiP2pConfig config = new WifiP2pConfig();
    config.deviceAddress = selectedDevice.deviceAddress; // replace with actual device address

manager.connect(channel, config, new WifiP2pManager.ActionListener() {
        public void onSuccess;() {
            // Connection successful, proceed with data exchange
        }

        public void onFailure;(int reason;) {
            // Connection failed, handle the error
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

}