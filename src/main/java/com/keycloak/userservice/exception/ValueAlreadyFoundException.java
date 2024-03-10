package com.keycloak.userservice.exception;

public class ValueAlreadyFoundException extends RuntimeException {
    public ValueAlreadyFoundException(String s) {
        super(s);
    }
}