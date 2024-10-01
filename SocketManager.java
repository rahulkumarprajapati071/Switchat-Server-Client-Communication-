package com.example.switchat.model;

import java.net.Socket;

public class SocketManager {
    private static Socket socket;

    public static Socket getSocket(){
        return socket;
    }

    public static void setSocket(Socket s){
        socket = s;
    }
}
