package com.example;

import com.example.CPABE;

import java.io.*;
import java.net.*;

public class App {
    public static void main(String[] args) {
        try {
            // 初始化CPABE系统
            CPABE cpabe = new CPABE();
            cpabe.setup();
            System.out.println("CPABE system setup completed.");

            // 启动服务器，监听请求
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("AAC Server started at port 8080");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new RequestHandler(socket, cpabe)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class RequestHandler implements Runnable {
    private Socket socket;
    private CPABE cpabe;

    public RequestHandler(Socket socket, CPABE cpabe) {
        this.socket = socket;
        this.cpabe = cpabe;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request = reader.readLine();
            if (request.startsWith("GET_PUB_MSK")) {
                String pubKey = cpabe.getPubkey();
                String mskKey = cpabe.getMskkey();
                writer.println(pubKey + "\n" + mskKey);
            } else if (request.startsWith("GET_PRIKEY")) {
                String attributes = request.split(":")[1];
                String priKey = cpabe.getPrikey(attributes);
                writer.println(priKey);
            } else {
                writer.println("Invalid request");
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}
