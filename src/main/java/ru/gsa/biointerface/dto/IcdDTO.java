package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) заболеване по международной классификации болезней (ICD)
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Data
@Builder
@Schema(name = "Icd", description = "ICD disease code")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class IcdDTO implements Serializable, Comparable<IcdDTO> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "ICD ID")
    private Integer id;

    /**
     * Наименование заболевания по ICD
     */
    @Schema(description = "Name", required = true)
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    private String name;

    /**
     * Версия ICD
     */
    @Schema(description = "Version", required = true)
    @Min(value = 10, message = "Version can't be lass then 10")
    @Max(value = 99, message = "Version can't be more than 99")
    private Integer version;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IcdDTO icdDTO = (IcdDTO) o;
        return Objects.equals(id, icdDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(IcdDTO o) {
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
        return "Icd{" +
                "id=" + id +
                ", ICD='" + name + '\'' +
                ", version=" + version +
                '}';
    }
}
