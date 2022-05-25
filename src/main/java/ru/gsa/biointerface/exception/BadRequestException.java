package ru.gsa.biointerface.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/01/2022
 */
public class BadRequestException extends ResponseStatusException {

    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
