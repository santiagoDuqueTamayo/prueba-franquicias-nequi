package com.nequi.franchises.franchises.domain.exception;

public class StockInsufficientException extends DomainException {
    public StockInsufficientException(int available, int requested) {
        super("Insufficient stock. Available: " + available + ", requested: " + requested);
    }
}
