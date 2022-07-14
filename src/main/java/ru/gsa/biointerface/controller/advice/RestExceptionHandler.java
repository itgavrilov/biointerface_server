package ru.gsa.biointerface.controller.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.gsa.biointerface.domain.ErrorResponse;

import java.time.LocalDateTime;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/01/2022
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatus()).body(
                ErrorResponse.builder()
                        .message(e.getMessage())
                        .status(e.getStatus())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
