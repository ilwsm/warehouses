package org.kinocat.warehouses.rest.exceptions;

public class RestException extends RuntimeException {

    private final int status;

    public RestException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
