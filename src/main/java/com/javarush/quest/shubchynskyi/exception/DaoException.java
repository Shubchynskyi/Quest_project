package com.javarush.quest.shubchynskyi.exception;

public class DaoException extends RuntimeException{
    public DaoException() {
        super();
    }
    @SuppressWarnings("unused")
    public DaoException(String message) {
        super(message);
    }
    @SuppressWarnings("unused")
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
    @SuppressWarnings("unused")
    public DaoException(Throwable cause) {
        super(cause);
    }
}
