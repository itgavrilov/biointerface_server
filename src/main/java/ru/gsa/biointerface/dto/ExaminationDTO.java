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

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Builder
@Data
@Schema(name = "Examination", description = "result of biopotential measurements")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ExaminationDTO implements Serializable, Comparable<ExaminationDTO> {
    static final long SerialVersionUID = 1L;

    @Schema(description = "examination ID")
    @NotNull(message = "Id can't be null")
    private int id;

    @Schema(description = "examination start time")
    @NotNull(message = "Start time can't be null")
    @Past(message = "Start time should be in past")
    private LocalDateTime starttime;

    @Schema(description = "patient ID")
    @NotNull(message = "PatientId can't be null")
    private int patientId;

    @Schema(description = "device ID")
    @NotNull(message = "DeviceId can't be null")
    private int deviceId;

    @Schema(description = "examination comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExaminationDTO entity = (ExaminationDTO) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ExaminationDTO o) {
        if (o == null || getClass() != o.getClass()) return -1;
        int result = 0;

        if (id > o.id) {
            result = 1;
        } else if (id < o.id) {
            result = -1;
        }

        return result;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        return "Examination{" +
                "id=" + id +
                ", datetime=" + formatter.format(starttime) +
                ", patientRecord_id=" + patientId +
                ", device_id=" + deviceId +
                '}';
    }
}
