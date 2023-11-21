package com.hellocorp.example.iam.domain.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5 {
    public static final String KEY_MD5 = "MD5";

    public static String encrypt(String inputStr) {
        try {
            MessageDigest md = MessageDigest.getInstance(KEY_MD5);
            byte[] inputData = inputStr.getBytes();
            md.update(inputData);
            return new BigInteger(md.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }

}