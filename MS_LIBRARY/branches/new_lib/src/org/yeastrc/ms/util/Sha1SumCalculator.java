package org.yeastrc.ms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1SumCalculator {

    private static final Sha1SumCalculator instance = new Sha1SumCalculator();
    
    private Sha1SumCalculator() {}
    
    public static Sha1SumCalculator instance() {
        return instance;
    }
    
    public String sha1SumFor(File file) throws NoSuchAlgorithmException, IOException {
        FileInputStream inStr;
        inStr = new FileInputStream(file);
        return sha1SumFor(inStr);
    }

    public String sha1SumFor(InputStream inStr) throws NoSuchAlgorithmException, IOException {
        
        MessageDigest digest;
        digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            bytesRead = inStr.read(buffer);
            while (bytesRead > 0) {
                digest.update(buffer, 0, bytesRead);
                bytesRead = inStr.read(buffer);
            }
        }
        finally {
            if (inStr != null) inStr.close();
        }
        byte[] digested = digest.digest();
        return hexStringFor(digested);
    }
    
    public String hexStringFor(byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            buf.append(Integer.toHexString(0xFF & bytes[i]));
        }
        return buf.toString();
    }
}
