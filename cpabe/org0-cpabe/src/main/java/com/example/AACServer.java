package com.example;

import co.junwei.cpabe.Cpabe;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AACServer {
    private static final int PORT = 8080;
    private Cpabe cpabe;
    private String pubfile;
    private String mskfile;

    public AACServer() {
        try {
            cpabe = new Cpabe();
            pubfile = "src/main/java/com/example/keys/pub_key.txt";
            mskfile = "src/main/java/com/example/keys/msk_key.txt";
            cpabe.setup(pubfile, mskfile);
            System.out.println("Pubfile content: " + new String(Files.readAllBytes(Paths.get(pubfile)), StandardCharsets.UTF_8));
            System.out.println("Mskfile content: " + new String(Files.readAllBytes(Paths.get(mskfile)), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPubkeyAndMskkey() {
        try {
            String pubKey = encodeFileToBase64(pubfile);
            String mskKey = encodeFileToBase64(mskfile);
            String response = pubKey + "---SPLIT---" + mskKey;
            System.out.println("Generated response: " + response);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPrikey(String attributes) {
        try {
            String keyFilePath = "src/main/java/com/example/keys/prikey.txt";
            System.out.println("Generating private key with attributes: " + attributes);
            cpabe.keygen(pubfile, keyFilePath, mskfile, attributes);
            return encodeFileToBase64(keyFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("AAC Server started at port " + PORT);
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                    String request = reader.readLine();
                    if (request.startsWith("GET_PUB_MSK")) {
                        String response = getPubkeyAndMskkey();
                        writer.println(response);
                    } else if (request.startsWith("GET_PRIKEY")) {
                        String attributes = request.split("\\|")[2];
                        String response = getPrikey(attributes);
                        writer.println(response);
                    } else {
                        writer.println("Invalid request");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AACServer server = new AACServer();
        server.start();
    }

    private String encodeFileToBase64(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(fileContent);
    }
}

