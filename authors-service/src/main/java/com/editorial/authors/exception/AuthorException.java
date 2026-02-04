package com.editorial.authors.exception;

public class AuthorException extends RuntimeException {
    public AuthorException(String message) {
        super(message);
    }

    public AuthorException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class AuthorNotFoundException extends AuthorException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}

public class AuthorAlreadyExistsException extends AuthorException {
    public AuthorAlreadyExistsException(String message) {
        super(message);
    }
}
