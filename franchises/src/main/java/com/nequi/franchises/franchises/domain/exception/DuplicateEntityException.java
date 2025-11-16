package com.nequi.franchises.franchises.domain.exception;

public class DuplicateEntityException extends DomainException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}