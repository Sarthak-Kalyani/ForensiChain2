package com.sdcems.sdcems.service;

import java.security.MessageDigest;
import java.io.InputStream;

public class HashService {

    public static String generateHash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String generateFileHash(InputStream inputStream) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] hash = md.digest();
            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}