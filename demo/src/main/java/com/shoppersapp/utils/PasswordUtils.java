package com.shoppersapp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PasswordUtils {
    /**
     * Attempts to hash the password.
     * 
     * @return the password or null if it couldn't be hashed
     */
    public static byte[] hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm exception" + e);
            return null;
        }
    }

    /**
     * Verifies that the provided password matches the stored hash.
     */
    public static boolean verifyPassword(String inputPassword, byte[] storedHash) {
        byte[] inputHash = hashPassword(inputPassword);
        return inputHash != null && Arrays.equals(inputHash, storedHash);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String hex) {
        if (hex.startsWith("\\x")) {
            hex = hex.substring(2); // remove \x prefix
        }
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}