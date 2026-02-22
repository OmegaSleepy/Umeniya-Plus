package org.martin.exceptions;

public class InvalidPassword extends RuntimeException {
    public InvalidPassword(String message) {
        super(message);
    }
}
