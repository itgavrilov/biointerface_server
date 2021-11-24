package ru.gsa.biointerface.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ExaminationDTO implements Serializable, Comparable<ExaminationDTO> {
    @NotNull(message = "Id can't be null")
    private int id;

    @NotNull(message = "Start time can't be null")
    @Past(message = "Start time should be in past")
    private LocalDateTime starttime;

    @NotNull(message = "PatientId can't be null")
    private int patientId;

    @NotNull(message = "DeviceId can't be null")
    private int deviceId;

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