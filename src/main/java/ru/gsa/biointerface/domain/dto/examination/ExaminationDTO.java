package ru.gsa.biointerface.domain.dto.examination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Past;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) исследования
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "Examination", description = "Result of biopotential measurements")
public class ExaminationDTO extends ExaminationUpdateDTO implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Examination ID")
    private UUID id;

    /**
     * Время начала исследования {@link LocalDateTime}
     */
    @Schema(description = "Examination start time", required = true)
    @Past(message = "Start time should be in past")
    private LocalDateTime datetime;

    /**
     * Дата создания
     */
    @Schema(description = "Creation date")
    private LocalDateTime creationDate;

    /**
     * Дата последнего изменений
     */
    @Schema(description = "Modify date")
    private LocalDateTime modifyDate;

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

        return datetime.compareTo(that.datetime);
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        return "Examination{" +
                "id=" + id +
                ", datetime=" + formatter.format(datetime) +
                ", patientRecord_id=" + patientId +
                ", device_id=" + deviceId +
                '}';
    }
}
