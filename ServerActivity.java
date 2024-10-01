package com.example.switchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.switchat.R;
import com.example.switchat.model.SocketManager;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerActivity extends AppCompatActivity {

    private static final Integer portInt = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_server_activity);

        TextView ipAddress = findViewById(R.id.ip_address);
        TextView port = findViewById(R.id.port);

        port.setText(String.valueOf(portInt));

        String ipAddresss = getLocalIpAddress();

        if (ipAddresss != null) {
            ipAddress.setText(ipAddresss);
        } else {
            ipAddress.setText("Unable to get IP address");
        }

        serverStarting(ipAddresss);

    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private void serverStarting(String ipAddresss) {
        new Thread(() -> {
            try {
                //create a server socket
                ServerSocket serverSocket = new ServerSocket(portInt);

                //wait for client connection
                Socket clientSocket = serverSocket.accept();
                SocketManager.setSocket(clientSocket);
//              //Once connected, redirect to chat screen
                runOnUiThread(() -> {
                    //Todo: something we need to pass
                    Intent intent = new Intent(ServerActivity.this,ChatActivity.class);
                    startActivity(intent);
                });
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }
}