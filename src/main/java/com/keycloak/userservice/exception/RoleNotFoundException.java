package com.keycloak.userservice.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleNotFound) {
        super(roleNotFound);
    }
}
