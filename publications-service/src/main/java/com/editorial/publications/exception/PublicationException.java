package com.editorial.publications.exception;

public class PublicationException extends RuntimeException {
    public PublicationException(String message) {
        super(message);
    }

    public PublicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class PublicationNotFoundException extends PublicationException {
    public PublicationNotFoundException(String message) {
        super(message);
    }
}

class AuthorNotFoundException extends PublicationException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}

class AuthorServiceException extends PublicationException {
    public AuthorServiceException(String message) {
        super(message);
    }

    public AuthorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

class PublicationInvalidStateException extends PublicationException {
    public PublicationInvalidStateException(String message) {
        super(message);
    }
}
