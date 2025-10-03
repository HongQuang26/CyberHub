package com.yourcompany.cyberhub.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing using SHA-256
 * Note: For production use, consider using BCrypt or Argon2
 */
public class PasswordHasher {
    
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Hash a password using SHA-256
     * @param password The plain text password
     * @return The hashed password in Base64 format
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against its hash
     * @param password The plain text password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        String hash = hashPassword(password);
        return hash.equals(hashedPassword);
    }
    
    /**
     * Generate a random salt (for future use with salted hashing)
     * @return A random salt string in Base64 format
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
