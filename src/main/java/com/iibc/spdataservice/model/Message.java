package com.iibc.spdataservice.model;

/**
 * Simple domain object for our API to return a message.
 */
public class Message {
    private final String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
