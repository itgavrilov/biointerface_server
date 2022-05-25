package ru.gsa.biointerface.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/01/2022
 */
@Data
@Builder
@Schema(name = "ErrorResponse", description = "error")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ErrorResponse {

    @Schema(description = "error message")
    private String message;

    @Schema(description = "error status")
    private HttpStatus status;

    @Schema(description = "error status")
    private LocalDateTime timestamp;
}
