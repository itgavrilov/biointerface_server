package ru.gsa.biointerface.controller.json;

import com.fasterxml.jackson.databind.util.ClassUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.gsa.biointerface.domain.dto.ErrorResponse;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Класс для перехвата исключений возникших при REST-запросе и формирования ответа клиенту
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/01/2022
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionRestControllerAdvice {

    /**
     * Перехватчик исключений
     *
     * @param exception Любой наследний не перехваченный другим ExceptionHandler {@link Exception}
     * @return Возврощаемый ответ клиенту {@link ResponseEntity<ErrorResponse>}
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleUndefinedException(Exception exception) {
        var message = "Неопределенная ошибка сервера: " + getRootCauseMessage(exception);

        return buildResponse(INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Перехватчик исключений бизнес-логики
     *
     * @param exception Исключение-наследник бизнес-логики {@link ResponseStatusException}
     * @return Возврощаемый ответ клиенту {@link ResponseEntity<ErrorResponse>}
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {

        return buildResponse(exception.getStatus(), exception.getReason());
    }

    /**
     * Получение сообщения о пречине исключения
     *
     * @param exception Исключение {@link Exception}
     * @return Сстрока сообщения
     */
    private String getRootCauseMessage(Exception exception) {
        Throwable rootCause = ClassUtil.getRootCause(exception);

        return ClassUtil.exceptionMessage(rootCause);
    }

    /**
     * Формирование ответа с сообщением об ошибке клиенту
     *
     * @param status Статус HTTP-запроса {@link HttpStatus}
     * @param msg    Сообщение об ошибке
     * @return Ответ с сообщением об ошибке {@link ResponseEntity<ErrorResponse>}
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String msg) {

        return ResponseEntity.status(status.value())
                .body(ErrorResponse.builder()
                        .code(status.value())
                        .status(status.name())
                        .message(msg)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
