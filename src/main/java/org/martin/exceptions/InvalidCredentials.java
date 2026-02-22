package org.martin.exceptions;

public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials(String message) {
        super(message);
    }
}
