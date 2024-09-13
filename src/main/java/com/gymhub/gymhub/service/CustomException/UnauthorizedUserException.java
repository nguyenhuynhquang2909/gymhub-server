package com.gymhub.gymhub.service.CustomException;

public class UnauthorizedUserException extends Exception{
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
