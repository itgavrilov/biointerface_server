package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) карточки пациента
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Data
@Builder
@Schema(name = "Patient", description = "patient record")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PatientDTO implements Serializable, Comparable<PatientDTO> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Patient ID")
    private Integer id;

    /**
     * Фамилия
     */
    @Schema(description = "Second name", required = true)
    @NotBlank(message = "Second name can't be blank")
    private String secondName;

    /**
     * Имя
     */
    @Schema(description = "First name", required = true)
    @NotBlank(message = "First name can't be blank")
    private String firstName;

    /**
     * Отчество
     */
    @Schema(description = "Patronymic")
    private String patronymic;

    /**
     * Дата рождения {@link LocalDateTime}
     */
    @Schema(description = "Birthday", required = true)
    @Past(message = "Birthday should be in past")
    private LocalDateTime birthday;

    /**
     * Идентификатор заболевания по ICD {@link IcdDTO#getId()}
     */
    @Schema(description = "ICD ID")
    private Integer icdId;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Max(value = 400, message = "Comment can't be more than 400 chars")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientDTO that = (PatientDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(PatientDTO o) {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return "PatientRecord{" +
                "id=" + id +
                ", second_name='" + secondName + '\'' +
                ", first_name='" + firstName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthday=" + formatter.format(birthday) +
                ", icd_id=" + icdId +
                '}';
    }
}
