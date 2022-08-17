package ru.gsa.biointerface.domain.dto.icd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) заболеване по международной классификации болезней (ICD)
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "IcdDTO", description = "ICD disease code")
public class IcdDTO extends IcdSaveOrUpdateDTO implements Serializable, Comparable<Object> {

    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "ICD ID")
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
        IcdSaveOrUpdateDTO that = (IcdSaveOrUpdateDTO) o;
        return name.equals(that.name) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        IcdSaveOrUpdateDTO that = (IcdSaveOrUpdateDTO) o;
        int result = name.compareTo(that.name);

        if (result == 0) result = version - that.version;

        return result;
    }

    @Override
    public String toString() {
        return "IcdDTO{" +
                "id=" + id +
                ", ICD='" + name + '\'' +
                ", version=" + version +
                ", comment=" + comment +
                '}';
    }
}
