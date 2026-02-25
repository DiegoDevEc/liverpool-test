package com.liverpool.liverpooltest.domain.exception;

public class PostalCodeNotFoundException extends RuntimeException {
    public PostalCodeNotFoundException(String postalCode) {
        super("No address found for postal code: " + postalCode);
    }
}
