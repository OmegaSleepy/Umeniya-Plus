package org.martin.exceptions;

public class MalformedPassword extends RuntimeException {
    public MalformedPassword(String message) {
        super(message);
    }
}
