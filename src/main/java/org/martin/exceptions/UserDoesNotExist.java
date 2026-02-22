package org.martin.exceptions;

public class UserDoesNotExist extends RuntimeException {
    public UserDoesNotExist(String message) {
        super(message);
    }
}
