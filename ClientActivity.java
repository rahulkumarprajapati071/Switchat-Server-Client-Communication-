package com.example.switchat.activity;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {

    private Socket socket;
    private ProgressBar progressBar;
    private static final String TAG = "ClientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_activity);

        EditText ipAddress = findViewById(R.id.ip_address);
        EditText port = findViewById(R.id.port);
        Button connectBtn = findViewById(R.id.connect_button);
        progressBar = findViewById(R.id.progress_bar);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ip = ipAddress.getText().toString();
                String portStr = port.getText().toString();

                if(validateInput(ip,portStr)){
                    connectToServer(ip,portStr);
                }else{
                    Toast.makeText(ClientActivity.this,"Invalid IP or Port",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInput(String ip, String portStr) {
        if(ip.isEmpty() || portStr.isEmpty()){
            return false;
        }
        try {
            int portInt = Integer.parseInt(portStr);
            return portInt > 1024 && portInt < 65535;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private void connectToServer(final String ipAddress, final String port) {
        //Todo: logic to connect to server & direct to chatScreenActivity
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected void onPreExecute(){
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    socket = new Socket(ipAddress, Integer.parseInt(port));
                    return socket.isConnected();
                }catch (IOException e){
                    Log.e(TAG,"Error connecting to server: "+e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success){
                progressBar.setVisibility(View.GONE);

                if(success){
                    //Move to chat screen
                    SocketManager.setSocket(socket);
                    Intent intent = new Intent(ClientActivity.this, ChatActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(ClientActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();



    }
}