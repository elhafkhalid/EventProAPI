package com.ex.eventpro_api.core.exception;

public class TicketAlreadyUsedException extends RuntimeException {
    public TicketAlreadyUsedException(String message) {
        super(message);
    }
}
