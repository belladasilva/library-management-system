package ui;

import java.util.regex.Pattern;

public final class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private InputValidator() {
    }

    public static String requiredText(String value, String fieldName) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    public static String email(String value) {
        String email = requiredText(value, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        return email;
    }

    public static int nonNegativeInt(String value, String fieldName) {
        String trimmed = requiredText(value, fieldName);
        try {
            int number = Integer.parseInt(trimmed);
            if (number < 0) {
                throw new IllegalArgumentException(fieldName + " must be zero or greater.");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a whole number.", exception);
        }
    }

    public static int positiveId(String value, String fieldName) {
        int id = nonNegativeInt(value, fieldName);
        if (id == 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero.");
        }
        return id;
    }
}
