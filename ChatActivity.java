package com.example.switchat.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.switchat.R;
import com.example.switchat.adapter.ChatAdapter;
import com.example.switchat.model.SocketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private Socket socket;
    private EditText messageInput;
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private ArrayList<String> chatMessages;
    private static final String TAG = "ChatActivity";

    // ExecutorService for background tasks
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        Button sendButton = findViewById(R.id.send_button);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Get the socket from the SocketManager
        socket = SocketManager.getSocket();

        // Start receiving messages
        startReceivingMessages();

        // Send message on button click
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageInput.setText("");  // Clear input field
                }
            }
        });
    }

    // Sending message using ExecutorService
    // Send message method with auto-scrolling
    private void sendMessage(String message) {
        chatMessages.add(message); // Add the message to the local list
        runOnUiThread(() -> {
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        });

        // Send the message over the socket
        executorService.execute(() -> {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
                outputStreamWriter.write(message + "\n");
                outputStreamWriter.flush(); // Make sure to flush to ensure the message is sent immediately
                Log.d(TAG, "Message sent: " + message); // Debugging log to verify
            } catch (IOException e) {
                Log.e(TAG, "Error sending message: " + e.getMessage());
            }
        });
    }


    // Receiving messages with auto-scrolling
    private void startReceivingMessages() {
        executorService.execute(() -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receivedMessage;
                while ((receivedMessage = bufferedReader.readLine()) != null) {
                    Log.d(TAG, "Message received: " + receivedMessage); // Debugging log

                    // Update the UI with the received message
                    String finalReceivedMessage = receivedMessage;
                    runOnUiThread(() -> {
                        chatMessages.add(finalReceivedMessage);
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error receiving message: " + e.getMessage());
            }
        });
    }


}
