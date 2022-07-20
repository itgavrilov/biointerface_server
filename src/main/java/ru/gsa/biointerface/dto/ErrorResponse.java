package ru.gsa.biointerface.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) сообщения об ошибке елиенту
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/01/2022
 */
@Data
@Builder
@Schema(name = "ErrorResponse", description = "error")
public class ErrorResponse {

    /**
     * Код статуса ошибки {@link HttpStatus#value()}
     */
    @Schema(description = "Code")
    private Integer code;

    /**
     * Статус ошибки {@link HttpStatus#name()}
     */
    @Schema(description = "Status")
    private String status;

    /**
     * Сообщение об ошибке
     */
    @Schema(description = "Message")
    private String message;

    /**
     * Дата/время возникновения ошибки
     */
    @Schema(description = "timestamp")
    private LocalDateTime timestamp;
}
