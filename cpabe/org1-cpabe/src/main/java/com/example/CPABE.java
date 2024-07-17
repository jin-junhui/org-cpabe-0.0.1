package com.example;

import co.junwei.cpabe.Cpabe;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CPABE {
    private Cpabe cpabe;
    private String pubfile;
    private String mskfile;

    public CPABE(String pubfile, String mskfile) {
        this.cpabe = new Cpabe();
        this.pubfile = pubfile;
        this.mskfile = mskfile;
    }

    public void setup() throws Exception {
        cpabe.setup(pubfile, mskfile);
    }

    public void keygen(String keyfile, String attributes) throws Exception {
        cpabe.keygen(pubfile, keyfile, mskfile, attributes);
    }

    public byte[] encrypt(String policy, String aesKey, String encfile) throws Exception {
        return cpabe.enc(pubfile, policy, aesKey, encfile);
    }

    public byte[] decrypt(String keyfile, byte[] encKey) throws Exception {
        return cpabe.dec(pubfile, keyfile, encKey);
    }
}