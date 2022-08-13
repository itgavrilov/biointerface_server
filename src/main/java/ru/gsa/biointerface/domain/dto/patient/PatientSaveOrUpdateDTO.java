package ru.gsa.biointerface.domain.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.gsa.biointerface.domain.dto.icd.IcdDTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) карточки пациента для создания или обновления
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 12/08/2022
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "PatientSaveOrUpdateDTO", description = "Patient record for save or update")
public class PatientSaveOrUpdateDTO implements Serializable {

    static final long SerialVersionUID = 1L;

    /**
     * Фамилия
     */
    @Schema(description = "Second name", required = true)
    @NotBlank(message = "Second name can't be blank")
    protected String secondName;

    /**
     * Имя
     */
    @Schema(description = "First name", required = true)
    @NotBlank(message = "First name can't be blank")
    protected String firstName;

    /**
     * Отчество
     */
    @Schema(description = "Patronymic")
    protected String patronymic;

    /**
     * Дата рождения {@link LocalDate}
     */
    @Schema(description = "Birthday", required = true)
    @Past(message = "Birthday should be in past")
    protected LocalDate birthday;

    /**
     * Идентификатор заболевания по ICD {@link IcdDTO#getId()}
     */
    @Schema(description = "ICD ID")
    protected UUID icdId;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Max(value = 400, message = "Comment can't be more than 400 chars")
    protected String comment;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return "PatientSaveOrUpdateDTO{" +
                ", second_name='" + secondName + '\'' +
                ", first_name='" + firstName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthday=" + formatter.format(birthday) +
                ", icd_id=" + icdId +
                ", comment=" + comment +
                '}';
    }
}
