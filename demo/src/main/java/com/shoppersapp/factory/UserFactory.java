package com.shoppersapp.factory;

import java.time.LocalDate;
import java.util.regex.Pattern;
import com.shoppersapp.model.User;
import java.time.Period;

/**
 * Factory class for user objects.
 * Responsible for validation and creation of user objects.
 */
public class UserFactory {
    // A constant used to define the minimum age of the product
    private static final int MIN_AGE = 18;
    // A constant used to define the acceptable phone number format
    private static final String PHONE_NUMBER_FORMAT = "^\\+?[1-9][0-9]{7,14}$";
    // A constant used to define the acceptable email format
    private static final String EMAIL_FORMAT = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    // private static final String EMAIL_FORMAT = "^[A-Za-z0-9+_.-]+@(.+)$";

    /**
     * Creates a user object so long as all passed arguments are valid.
     * This function should only be used in the context of user registration.
     * This function does not inherently validate the requested object. Call the
     * validation function before using.
     * 
     * @return a new User object
     */
    public static User createUser(String firstName, String lastName, LocalDate dateOfBirth, String phoneNumber,
            String email, String address, byte[] passwordHash) throws IllegalArgumentException {
        validateUser(firstName, lastName, dateOfBirth, phoneNumber, email, address);
        return new User(firstName, lastName, sanitizeEmail(email), phoneNumber, dateOfBirth, address, passwordHash);
    }

    /**
     * Creates a User object so long as all passed arguments are valid.
     * This function should only be used in the context of retrieving user details
     * from the database.
     * This function does not inherently validate the requested object. Call the
     * validation function before using. *
     * 
     * @return a new User object
     */
    public static User createUser(Integer userId, String firstName, String lastName, LocalDate dateOfBirth,
            String phoneNumber,
            String email, String address, byte[] passwordHash) throws IllegalArgumentException {

        validateUser(userId, firstName, lastName, dateOfBirth, phoneNumber, email, address);
        return new User(userId, firstName, lastName, phoneNumber, sanitizeEmail(email), dateOfBirth, address,
                passwordHash);
    }

    private static String sanitizeEmail(String email) {
        if (email == null)
            throw new NullPointerException("Email cannot be null");
        String sanitized = email.trim();
        sanitized = sanitized.replaceAll("\\p{C}", "");
        return sanitized;
    }

    /**
     * Validates a user's details
     * This function should only be used in the context of registering a new user
     * 
     * @return true if the provided parameters are valid to create a User
     * @throws NullPointerException if any passed parameter is null
     */
    private static boolean validateUser(String firstName, String lastName, LocalDate dateOfBirth, String phoneNumber,
            String email,
            String address) throws NullPointerException {

        if (firstName == null || lastName == null || dateOfBirth == null || phoneNumber == null || email == null
                || address == null) {
            throw new NullPointerException("Null value detected in user details.");
        }

        if (validateName(firstName) && validateName(lastName) && validateDateOfBirth(dateOfBirth)
                && validatePhoneNumber(phoneNumber) &&
                validateEmail(email)) {
            return true;
        }
        return false;
    }

    /**
     * Validates a user's details
     * This function should only be used in the context of retrieving user details
     * from the database
     * 
     * @return true if the provided parameters are valid to create a User
     * @throws NullPointerException if any passed parameter is null
     */
    private static boolean validateUser(Integer userId, String firstName, String lastName, LocalDate dateOfBirth,
            String phoneNumber, String email, String address) throws NullPointerException {

        if (userId == null || firstName == null || lastName == null || dateOfBirth == null || phoneNumber == null
                || email == null
                || address == null) {
            throw new NullPointerException("Null value detected in user details.");
        }

        if (validateId(userId) && validateName(firstName) && validateName(lastName) && validateDateOfBirth(dateOfBirth)
                && validatePhoneNumber(phoneNumber) &&
                validateEmail(email)) {
            return true;
        }
        return false;
    }

    /**
     * @return true if the passed name is valid
     * @throws IllegalArgumentException if the name is empty or it contains non
     *                                  alphabetic characters
     */
    private static boolean validateName(String name) throws IllegalArgumentException {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        } else if (!name.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("Name must only contain alphabetic characters");
        }
        return true;
    }

    /**
     * @return true if the date of birth is valid
     * @throws IllegalArgumentException if the date of birth is in the future or it
     *                                  exceeds MIN_AGE
     */
    private static boolean validateDateOfBirth(LocalDate dateOfBirth) throws IllegalArgumentException {
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        LocalDate today = LocalDate.now();
        int age = Period.between(dateOfBirth, today).getYears();
        if (age < MIN_AGE) {
            throw new IllegalArgumentException("Age must be at least 18 years");
        }
        return true;
    }

    /**
     * @return true if the phone number is valid
     * @throws IllegalArgumentException if the phone number is not in the form given
     *                                  in PHONE_NUMBER_FORMAT
     */
    private static boolean validatePhoneNumber(String phoneNumber) throws IllegalArgumentException {
        if (!Pattern.matches(PHONE_NUMBER_FORMAT, phoneNumber)) {
            throw new IllegalArgumentException("Phone number not in correct format");
        }
        return true;
    }

    /**
     * @return true if the email is valid
     * @throws IllegalArgumentException if the email is not in the form given in
     *                                  EMAIL_FORMAT
     */
    private static boolean validateEmail(String email) throws IllegalArgumentException {
        if (!Pattern.matches(EMAIL_FORMAT, email)) {
            throw new IllegalArgumentException("Email not in correct format");
        }
        return true;
    }

    /**
     * @return true if the ID is valid
     * @throws IllegalArgumentException if the ID is not a positive integer
     */
    private static boolean validateId(Integer userId) throws IllegalArgumentException {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is not a positive integer");
        }
        return true;
    }
}
