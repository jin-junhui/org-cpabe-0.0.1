package com.example;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class NodeServer {
    private static final int PORT = 8082;
    private static final String AAC_HOST = "org0-cpabe";
    private static final int AAC_PORT = 8080;
    private static final String PUB_KEY_PATH = "src/main/java/com/example/keys/pub_key";
    private static final String MSK_KEY_PATH = "src/main/java/com/example/keys/msk_key";
    private static final String PRI_KEY_PATH = "src/main/java/com/example/keys/prikey";

    public static void main(String[] args) {
        try {
            CPABE cpabe = new CPABE(PUB_KEY_PATH, MSK_KEY_PATH);
            setupCPABEKeys();
            // 其他初始化代码
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decodeFileFromBase64(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        byte[] decodedContent = Base64.getDecoder().decode(new String(fileContent, StandardCharsets.UTF_8));
        Files.write(Paths.get(filePath), decodedContent);
    }

    private static void setupCPABEKeys() throws IOException {
        try (Socket socket = new Socket(AAC_HOST, AAC_PORT)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer.println("GET_PUB_MSK");
            String response = reader.readLine();
            System.out.println("Response from AACServer: " + response); // 调试信息

            // 使用自定义分隔符解析响应
            String[] keys = response.split("---SPLIT---");
            System.out.println("Keys length: " + keys.length); // 调试信息
            if (keys.length < 2) {
                throw new IOException("Invalid response from AACServer");
            }

            System.out.println("Base64 Encoded PubKey: " + keys[0]); // 调试信息
            System.out.println("Base64 Encoded MskKey: " + keys[1]); // 调试信息

            Files.write(Paths.get(PUB_KEY_PATH), keys[0].getBytes(StandardCharsets.UTF_8));
            Files.write(Paths.get(MSK_KEY_PATH), keys[1].getBytes(StandardCharsets.UTF_8));
            decodeFileFromBase64(PUB_KEY_PATH);
            decodeFileFromBase64(MSK_KEY_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error setting up CPABE keys", e);
        }
    }
}

