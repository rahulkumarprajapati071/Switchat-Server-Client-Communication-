package com.example.myclient;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ClientActivity";
    private static final int PORT = 50512;
    private Socket socket;
    private PrintWriter output;

    private TextView clientStatusText;
    private EditText ipEditText;
    private Button connectBtn;
    private Button changeServerColorRedBtn;
    private Button changeServerColorBlueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientStatusText = findViewById(R.id.clientStatusText);
        ipEditText = findViewById(R.id.ipEditText);
        connectBtn = findViewById(R.id.connectBtn);
        changeServerColorRedBtn = findViewById(R.id.changeServerColorRedBtn);
        changeServerColorBlueBtn = findViewById(R.id.changeServerColorBlueBtn);

        connectBtn.setOnClickListener(v -> connectToServer());
        changeServerColorRedBtn.setOnClickListener(v -> sendColorChange("RED"));
        changeServerColorBlueBtn.setOnClickListener(v -> sendColorChange("BLUE"));
    }

    private void connectToServer() {
        String serverIp = ipEditText.getText().toString().trim();
        if (serverIp.isEmpty()) {
            clientStatusText.setText("Please enter a valid IP address.");
            return;
        }

        new Thread(() -> {
            try {
                socket = new Socket(serverIp, PORT);
                Log.d(TAG, "Connected to server: " + socket.getInetAddress().getHostAddress());
                runOnUiThread(() -> {
                    clientStatusText.setText("Connected to server: " + serverIp);
                    changeServerColorRedBtn.setVisibility(View.VISIBLE);
                    changeServerColorBlueBtn.setVisibility(View.VISIBLE);
                });

                output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String messageFromServer;
                while ((messageFromServer = input.readLine()) != null) {
                    Log.d(TAG, "Message received from server: " + messageFromServer);
                    if (messageFromServer.startsWith("CHANGE_COLOR:")) {
                        String color = messageFromServer.split(":")[1];
                        runOnUiThread(() -> changeClientColor(color)); // Update UI on the main thread
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to server: " + e.getMessage());
                runOnUiThread(() -> clientStatusText.setText("Error connecting to server: " + e.getMessage()));
            }
        }).start();
    }

    private void sendColorChange(String color) {
        if (output != null) {
            new Thread(() -> {
                output.println("CHANGE_COLOR:" + color);
                Log.d(TAG, "Sent color change command to server: " + color);
            }).start();
        }
    }

    private void changeClientColor(String color) {
        if ("RED".equals(color)) {
            findViewById(R.id.main).setBackgroundColor(Color.RED);
        } else if ("BLUE".equals(color)) {
            findViewById(R.id.main).setBackgroundColor(Color.BLUE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket: " + e.getMessage());
            }
        }
    }
}
