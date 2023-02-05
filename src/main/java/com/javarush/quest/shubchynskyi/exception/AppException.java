package com.javarush.quest.shubchynskyi.exception;

public class AppException extends RuntimeException{
    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    @SuppressWarnings("unused")
    public AppException(Throwable cause) {
        super(cause);
    }
}
