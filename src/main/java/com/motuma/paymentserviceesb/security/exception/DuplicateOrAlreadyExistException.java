package com.motuma.paymentserviceesb.security.exception;

public class DuplicateOrAlreadyExistException extends RuntimeException {
    public DuplicateOrAlreadyExistException(String message) {
        super(message);
    }
}
