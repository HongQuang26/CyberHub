package com.yourcompany.cyberhub.util;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Utility class for formatting money amounts with thousand separators
 */
public class MoneyFormatter {
    
    private static final DecimalFormat formatter;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        formatter = new DecimalFormat("#,##0", symbols);
        formatter.setGroupingSize(3);
    }
    
    /**
     * Format a BigDecimal amount with thousand separators (dots)
     * Example: 1000000 -> "1.000.000"
     */
    public static String format(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return formatter.format(amount);
    }
    
    /**
     * Format a long amount with thousand separators (dots)
     */
    public static String format(long amount) {
        return formatter.format(amount);
    }
    
    /**
     * Parse a formatted string back to BigDecimal
     * Example: "1.000.000" -> 1000000
     */
    public static BigDecimal parse(String formattedAmount) throws ParseException {
        if (formattedAmount == null || formattedAmount.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        // Remove dots (thousand separators) and parse
        String cleaned = formattedAmount.replaceAll("\\.", "").trim();
        return new BigDecimal(cleaned);
    }
    
    /**
     * Apply formatting to a JTextField so it automatically formats as user types
     */
    public static void applyMoneyFormat(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new MoneyDocumentFilter());
    }
    
    /**
     * DocumentFilter that automatically formats numbers with thousand separators
     */
    private static class MoneyDocumentFilter extends DocumentFilter {
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                throws BadLocationException {
            String newStr = string.replaceAll("[^0-9]", "");
            if (!newStr.isEmpty()) {
                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
                sb.insert(offset, newStr);
                String formatted = formatInput(sb.toString());
                replace(fb, 0, fb.getDocument().getLength(), formatted, attr);
            }
        }
        
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String unformatted = currentText.replaceAll("\\.", "");
            
            // Calculate the real position in unformatted string
            int realOffset = getRealOffset(currentText, offset);
            
            if (realOffset >= 0 && realOffset < unformatted.length()) {
                StringBuilder sb = new StringBuilder(unformatted);
                int endPos = Math.min(realOffset + length, sb.length());
                sb.delete(realOffset, endPos);
                String formatted = formatInput(sb.toString());
                replace(fb, 0, fb.getDocument().getLength(), formatted, null);
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            String newStr = text.replaceAll("[^0-9]", "");
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String unformatted = currentText.replaceAll("\\.", "");
            
            int realOffset = getRealOffset(currentText, offset);
            
            StringBuilder sb = new StringBuilder(unformatted);
            if (realOffset >= 0 && realOffset <= sb.length()) {
                int endPos = Math.min(realOffset + length, sb.length());
                sb.replace(realOffset, endPos, newStr);
                String formatted = formatInput(sb.toString());
                super.replace(fb, 0, fb.getDocument().getLength(), formatted, attrs);
            }
        }
        
        private String formatInput(String input) {
            if (input.isEmpty()) {
                return "";
            }
            try {
                long value = Long.parseLong(input);
                return formatter.format(value);
            } catch (NumberFormatException e) {
                return input;
            }
        }
        
        private int getRealOffset(String formatted, int visualOffset) {
            int realOffset = 0;
            int currentVisual = 0;
            for (int i = 0; i < formatted.length() && currentVisual < visualOffset; i++) {
                if (formatted.charAt(i) != '.') {
                    realOffset++;
                }
                currentVisual++;
            }
            return realOffset;
        }
    }
}
