package com.yourcompany.cyberhub.util;

import java.math.BigDecimal;

/**
 * Utility class for input validation
 */
public class InputValidator {
    
    /**
     * Validate username
     * - Must be 3-50 characters
     * - Only alphanumeric and underscore allowed
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{3,50}$");
    }
    
    /**
     * Validate password
     * - Must be at least 6 characters
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate full name
     * - Must be 2-100 characters
     * - Allow letters, spaces, and Vietnamese characters
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        String trimmed = fullName.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }
    
    /**
     * Validate amount (for balance, top-up, etc.)
     * - Must be positive
     * - Must not exceed reasonable limits
     */
    public static boolean isValidAmount(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        // Amount must be positive and less than 100 million
        return amount.compareTo(BigDecimal.ZERO) > 0 
            && amount.compareTo(new BigDecimal("100000000")) < 0;
    }
    
    /**
     * Get validation error message for username
     */
    public static String getUsernameErrorMessage() {
        return "Tên đăng nhập phải có 3-50 ký tự và chỉ chứa chữ cái, số và dấu gạch dưới.";
    }
    
    /**
     * Get validation error message for password
     */
    public static String getPasswordErrorMessage() {
        return "Mật khẩu phải có ít nhất 6 ký tự.";
    }
    
    /**
     * Get validation error message for full name
     */
    public static String getFullNameErrorMessage() {
        return "Họ và tên phải có 2-100 ký tự.";
    }
    
    /**
     * Get validation error message for amount
     */
    public static String getAmountErrorMessage() {
        return "Số tiền phải là số dương và không vượt quá 100 triệu.";
    }
}
