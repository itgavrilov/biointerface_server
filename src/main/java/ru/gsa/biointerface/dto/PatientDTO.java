package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
@Schema(name = "Patient", description = "patient record")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PatientDTO implements Serializable, Comparable<PatientDTO> {
    static final long SerialVersionUID = 1L;

    @Schema(description = "patient record ID")
    @NotNull(message = "Id can't be null")
    @Min(value = 1, message = "Id can't be lass then 1")
    private int id;

    @Schema(description = "patient second name")
    @NotBlank(message = "Second name can't be blank")
    @Size(min = 3, max = 20, message = "Second name should be have chars between 3-20")
    private String secondName;

    @Schema(description = "patient first name")
    @NotBlank(message = "First name can't be blank")
    @Size(min = 3, max = 20, message = "First name should be have chars between 3-20")
    private String firstName;

    @Schema(description = "patient patronymic")
    @NotBlank(message = "Patronymic can't be blank")
    @Size(min = 3, max = 20, message = "Patronymic should be have chars between 3-20")
    private String patronymic;

    @Schema(description = "patient birthday")
    @NotNull(message = "Birthday can't be null")
    @Past(message = "Birthday should be in past")
    private LocalDateTime birthday;

    @Schema(description = "ICD ID")
    private int icdId;

    @Schema(description = "patient comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
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
