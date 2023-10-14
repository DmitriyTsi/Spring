package com.tsyhanok.restapi.response;

import java.util.UUID;

public class UserResponse {
    private String message;
    private UUID id;

    public UserResponse(String message, UUID id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public UUID getUserId() {
        return id;
    }
}