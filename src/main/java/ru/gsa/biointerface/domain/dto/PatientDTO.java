package ru.gsa.biointerface.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) карточки пациента
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "Patient", description = "patient record")
public class PatientDTO implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Patient ID")
    private UUID id;

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
    private UUID icdId;

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
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        PatientDTO that = (PatientDTO) o;

        int result = secondName.compareTo(that.secondName);
        if (result == 0) result = firstName.compareTo(that.firstName);
        if (result == 0) result = patronymic.compareTo(that.patronymic);
        if (result == 0) result = birthday.compareTo(that.birthday);

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
