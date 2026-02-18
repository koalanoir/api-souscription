package com.koalanoir.souscription.domain.exceptions;

/**
 * Exception de base pour les exceptions du domaine métier
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
