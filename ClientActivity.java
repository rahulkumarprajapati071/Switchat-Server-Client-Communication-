package com.example.switchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.switchat.R;
import com.example.switchat.model.SocketManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientActivity extends AppCompatActivity {
    private Socket socket;
    private ProgressBar progressBar;
    private static final String TAG = "ClientActivity";

    // Create a single-threaded executor for background tasks
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client);

        EditText ipAddress = findViewById(R.id.ip_address);
        EditText port = findViewById(R.id.port);
        Button connectBtn = findViewById(R.id.connect_button);
        progressBar = findViewById(R.id.progress_bar);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipAddress.getText().toString();
                String portStr = port.getText().toString();
                if (validateInput(ip, portStr)) {
                    connectToServer(ip, portStr);
                } else {
                    Toast.makeText(ClientActivity.this, "Invalid IP or Port", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInput(String ip, String portStr) {
        if (ip.isEmpty() || portStr.isEmpty()) {
            return false;
        }
        try {
            int portInt = Integer.parseInt(portStr);
            return portInt > 1024 && portInt < 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void connectToServer(final String ipAddress, final String port) {
        Log.d(TAG, "Attempting to connect to server at IP: " + ipAddress + " and Port: " + port);
        progressBar.setVisibility(View.VISIBLE);

        Future<Boolean> future = executorService.submit(() -> {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ipAddress, Integer.parseInt(port)), 5000); // 5-second timeout
                return socket.isConnected();
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to server: " + e.getMessage());
                return false;
            }
        });

        executorService.submit(() -> {
            try {
                boolean success = future.get();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (success) {
                        Log.d(TAG, "Connected to server at " + ipAddress + ":" + port);

                        // Store the socket and move to chat
                        SocketManager.setSocket(socket);
                        Intent intent = new Intent(ClientActivity.this, ChatActivity.class);
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "Failed to connect to server");
                        Toast.makeText(ClientActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error in executor: " + e.getMessage());
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        });
    }

}
