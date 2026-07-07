package it.svg.crud.exception;

public class CrudDataAccessException extends RuntimeException {
    public CrudDataAccessException(String message) { super(message); }
    public CrudDataAccessException(String message, Throwable cause) { super(message, cause); }
}
