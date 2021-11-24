package ru.gsa.biointerface.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.*;
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
public class PatientDTO implements Serializable, Comparable<PatientDTO> {
    @NotNull(message = "Id can't be null")
    @Min(value = 1, message = "Id can't be lass then 1")
    private int id;

    @NotNull(message = "Second name can't be null")
    @NotBlank(message = "Second name can't be blank")
    @Size(min = 3, max = 20, message = "Second name should be have chars between 3-20")
    private String secondName;

    @NotNull(message = "First name can't be null")
    @NotBlank(message = "First name can't be blank")
    @Size(min = 3, max = 20, message = "First name should be have chars between 3-20")
    private String firstName;

    @NotNull(message = "Patronymic can't be null")
    @NotBlank(message = "Patronymic can't be blank")
    @Size(min = 3, max = 20, message = "Patronymic should be have chars between 3-20")
    private String patronymic;

    @NotNull(message = "Birthday can't be null")
    @Past(message = "Birthday should be in past")
    private LocalDateTime birthday;

    private int icdId;

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
