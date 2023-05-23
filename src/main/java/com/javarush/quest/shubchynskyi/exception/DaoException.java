package com.javarush.quest.shubchynskyi.exception;
@SuppressWarnings("unused")
public class DaoException extends RuntimeException{

    @SuppressWarnings("unused")
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
