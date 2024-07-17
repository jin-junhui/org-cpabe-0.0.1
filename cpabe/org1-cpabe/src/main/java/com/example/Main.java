package com.example;

import co.junwei.cpabe.Cpabe;
import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
    private static final String AAC_HOST = "org0-cpabe";
    private static final int AAC_PORT = 8080;
    private static final String PUB_KEY_PATH = "src/main/java/com/example/keys/pub_key.txt";
    private static final String MSK_KEY_PATH = "src/main/java/com/example/keys/msk_key.txt";
    private static final String ORG_NAME = "org1"; // Change to "org2" for org2-cpabe
    private static final String ATTRIBUTES = ORG_NAME.equals("org1") ? "org:org1 dept:dept1 level:1" : "org:org1 dept:dept2 level:2";
    private static final String KEYS_DIR = "src/main/java/com/example/keys/";
    private static final String FILES_DIR = "src/main/java/com/example/files/";
    private static final String ENCRYPTED_DIR = "src/main/java/com/example/encrypted/";
    private static final String DECRYPTED_DIR = "src/main/java/com/example/decrypted/";

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("Enter command: ");
                String command = reader.readLine();
                if (command == null || command.isEmpty()) {
                    continue;
                }
                if (command.startsWith("GET_PUB_MSK")) {
                    handleGetPubMsk();
                } else if (command.startsWith("GET_PRIKEY")) {
                    handleGetPrikey();
                } else if (command.startsWith("EncFile:")) {
                    String filename = command.split("\\|")[1].trim();
                    handleEncFile(filename);
                } else if (command.startsWith("DecFile:")) {
                    String filename = command.split("\\|")[1].trim();
                    handleDecFile(filename);
                } else {
                    System.out.println("Invalid command");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleGetPubMsk() {
        try (Socket socket = new Socket(AAC_HOST, AAC_PORT)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            writer.println("GET_PUB_MSK:" + ORG_NAME);
            String response = reader.readLine();
            System.out.println("Response from AACServer: " + response);

            String[] keys = response.split("---SPLIT---");
            if (keys.length < 2) {
                throw new IOException("Invalid response from AACServer");
            }

            Files.createDirectories(Paths.get(PUB_KEY_PATH).getParent());
            Files.createDirectories(Paths.get(MSK_KEY_PATH).getParent());

            Files.write(Paths.get(PUB_KEY_PATH), keys[0].getBytes(StandardCharsets.UTF_8));
            Files.write(Paths.get(MSK_KEY_PATH), keys[1].getBytes(StandardCharsets.UTF_8));
            decodeFileFromBase64(PUB_KEY_PATH);
            decodeFileFromBase64(MSK_KEY_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleGetPrikey() {
        try (Socket socket = new Socket(AAC_HOST, AAC_PORT)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            writer.println("GET_PRIKEY|" + ORG_NAME + "|" + ATTRIBUTES);
            String response = reader.readLine();
            System.out.println("Private Key response from AACServer: " + response);
            if (response == null || response.isEmpty()) {
                throw new IOException("Received null or empty private key response from AACServer");
            }

            String keyFilePath = KEYS_DIR + ORG_NAME + "_prikey.txt";
            Files.write(Paths.get(keyFilePath), response.getBytes(StandardCharsets.UTF_8));
            decodeFileFromBase64(keyFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleEncFile(String filename) {
        try {
            String policy = new String(Files.readAllBytes(Paths.get(FILES_DIR + filename))).split("\n")[0].trim();
            String encFilePath = ENCRYPTED_DIR + "encrypted_" + filename;
            String oriFilePath = FILES_DIR + filename;
            String fileContent = new String(Files.readAllBytes(Paths.get(oriFilePath)));
            Cpabe cpabe = new Cpabe();
            byte[] encData = cpabe.enc(PUB_KEY_PATH, policy, fileContent, encFilePath); // Adjust AES key handling as needed
            Files.write(Paths.get(encFilePath), encData);

            System.out.println("File encrypted: " + encFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleDecFile(String filename) {
        try {
            String encFilePath = ENCRYPTED_DIR + filename;
            String decFilePath = DECRYPTED_DIR + "decrypted_" + filename.replace("encrypted_", "");

            Cpabe cpabe = new Cpabe();
            byte[] encData = Files.readAllBytes(Paths.get(encFilePath));
            byte[] decData = cpabe.dec(PUB_KEY_PATH, KEYS_DIR + ORG_NAME + "_prikey.txt", encData);
            Files.write(Paths.get(decFilePath), decData);

            System.out.println("File decrypted: " + decFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decodeFileFromBase64(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        byte[] decodedContent = Base64.getDecoder().decode(new String(fileContent, StandardCharsets.UTF_8).trim());
        Files.write(Paths.get(filePath), decodedContent);
    }
}

