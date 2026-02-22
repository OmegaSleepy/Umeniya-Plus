package org.martin.exceptions;

public class MissingResource extends RuntimeException {
    public MissingResource(String message) {
        super(message);
    }
}
