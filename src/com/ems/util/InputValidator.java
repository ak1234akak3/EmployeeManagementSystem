package com.ems.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for validating user input across CLI and GUI.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s\\-\\(\\)]{7,20}$"
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Validates employee name.
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100 && trimmed.matches("^[a-zA-Z\\s.'\\-]+$");
    }

    /**
     * Validates employee email address.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates phone number.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates employee salary (must be strictly positive).
     */
    public static boolean isValidSalary(double salary) {
        return salary > 0 && salary <= 10_000_000.0;
    }

    /**
     * Validates non-empty string for department/designation.
     */
    public static boolean isNonEmpty(String str, int minLen, int maxLen) {
        if (str == null) return false;
        String trimmed = str.trim();
        return trimmed.length() >= minLen && trimmed.length() <= maxLen;
    }

    /**
     * Parses and validates a date string in YYYY-MM-DD format.
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            return date;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
