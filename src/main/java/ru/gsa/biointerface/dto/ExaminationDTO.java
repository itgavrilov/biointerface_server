package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) исследования
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Data
@Builder
@Schema(name = "Examination", description = "result of biopotential measurements")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ExaminationDTO implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "examination ID")
    private UUID id;

    /**
     * Время начала исследования {@link LocalDateTime}
     */
    @Schema(description = "examination start time", required = true)
    @Past(message = "Start time should be in past")
    private LocalDateTime datetime;

    /**
     * Идентификатор карточки пациента {@link PatientDTO#getId()}
     */
    @Schema(description = "patient ID", required = true)
    @NotNull(message = "PatientId can't be null")
    private UUID patientId;

    /**
     * Идентификатор контроллера биоинтерфейса {@link DeviceDTO#getId()}
     */
    @Schema(description = "device ID", required = true)
    @NotNull(message = "DeviceId can't be null")
    private UUID deviceId;

    /**
     * Комментарий
     */
    @Schema(description = "examination comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExaminationDTO that = (ExaminationDTO) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        ExaminationDTO that = (ExaminationDTO) o;

        return id.compareTo(that.id);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        return "Examination{" +
                "id=" + id +
                ", datetime=" + formatter.format(datetime) +
                ", patientRecord_id=" + patientId +
                ", device_id=" + deviceId +
                '}';
    }
}
