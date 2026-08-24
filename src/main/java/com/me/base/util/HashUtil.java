package com.me.base.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utility class for hashing operations.
 * Used for securely hashing refresh tokens before storage.
 * 
 * @author Base Project
 * @version 1.0
 */
public class HashUtil {
    
    private HashUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Hashes a string using SHA-256 algorithm.
     * Returns hexadecimal representation of the hash.
     * 
     * @param input string to hash
     * @return SHA-256 hash in hexadecimal format
     * @throws RuntimeException if SHA-256 algorithm is not available
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
