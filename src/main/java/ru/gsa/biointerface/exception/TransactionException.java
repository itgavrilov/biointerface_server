package ru.gsa.biointerface.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/01/2022
 */
public class TransactionException extends ResponseStatusException {

    public TransactionException() {
        super(HttpStatus.SERVICE_UNAVAILABLE);
    }

    public TransactionException(String reason) {
        super(HttpStatus.SERVICE_UNAVAILABLE, reason);
    }
}
