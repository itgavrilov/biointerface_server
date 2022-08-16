package ru.gsa.biointerface.domain.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "PatientDTO", description = "Patient record")
public class PatientDTO extends PatientSaveOrUpdateDTO implements Serializable, Comparable<Object> {

    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Patient ID")
    private UUID id;

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
        PatientSaveOrUpdateDTO that = (PatientSaveOrUpdateDTO) o;
        return secondName.equals(that.secondName)
                && firstName.equals(that.firstName)
                && patronymic.equals(that.patronymic)
                && birthday.equals(that.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secondName, firstName, patronymic, birthday);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        PatientSaveOrUpdateDTO that = (PatientSaveOrUpdateDTO) o;

        int result = secondName.compareTo(that.secondName);
        if (result == 0) result = firstName.compareTo(that.firstName);
        if (result == 0) result = patronymic.compareTo(that.patronymic);
        if (result == 0) result = birthday.compareTo(that.birthday);

        return result;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return "PatientDTO{" +
                "id=" + id +
                ", second_name='" + secondName + '\'' +
                ", first_name='" + firstName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthday=" + formatter.format(birthday) +
                ", icd_id=" + icdId +
                ", comment=" + comment +
                '}';
    }
}
