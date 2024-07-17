package com.example;

import co.junwei.cpabe.Cpabe;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CPABE {
    private Cpabe cpabe;
    private String pubfile;
    private String mskfile;

    public CPABE() {
        cpabe = new Cpabe();
        pubfile = "src/main/java/com/example/keys/pub_key.txt";
        mskfile = "src/main/java/com/example/keys/msk_key.txt";
    }

    public void setup() throws Exception {
        cpabe.setup(pubfile, mskfile);
    }

    public String getPubkey() throws IOException {
        return new String(Files.readAllBytes(Paths.get(pubfile)));
    }

    public String getMskkey() throws IOException {
        return new String(Files.readAllBytes(Paths.get(mskfile)));
    }

    public String getPrikey(String attributes) throws Exception {
        String keyFilePath = "src/main/java/com/example/cpabe/keys/prikey.txt";
        cpabe.keygen(pubfile, keyFilePath, mskfile, attributes);
        return new String(Files.readAllBytes(Paths.get(keyFilePath)));
    }
}
