package ru.gsa.biointerface.domain.dto.icd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * DTO (Data Transfer Object) заболеване по международной классификации болезней (ICD)
 * для создания или обновления
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 12/08/2022
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "IcdSaveOrUpdateDTO", description = "ICD disease code for save or update")
public class IcdSaveOrUpdateDTO implements Serializable {

    static final long SerialVersionUID = 1L;

    /**
     * Наименование заболевания по ICD
     */
    @Schema(description = "Name", required = true)
    @NotBlank(message = "Name can't be blank")
    protected String name;

    /**
     * Версия ICD
     */
    @Schema(description = "Version", required = true)
    @Min(value = 10, message = "Version can't be lass then 10")
    @Max(value = 99, message = "Version can't be more than 99")
    protected Integer version;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    protected String comment;

    @Override
    public String toString() {
        return "IcdSaveOrUpdateDTO{" +
                ", ICD='" + name + '\'' +
                ", version=" + version +
                ", comment=" + comment +
                '}';
    }
}
