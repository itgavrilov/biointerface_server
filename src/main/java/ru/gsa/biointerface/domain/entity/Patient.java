package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сущность карточки пациента
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "patient")
@Table(name = "patient")
public class Patient implements Serializable, Comparable<Patient> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Имя
     */
    @NotBlank(message = "First name can't be blank")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Фамилия
     */
    @NotBlank(message = "Second name can't be blank")
    @Column(name = "second_name", nullable = false)
    private String secondName;

    /**
     * Отчество
     */
    @Column(name = "patronymic")
    private String patronymic;

    /**
     * Дата рождения {@link LocalDateTime}
     */
    @Past(message = "Birthday should be in past")
    @Column(name = "birthday", nullable = false)
    private LocalDateTime birthday;

    /**
     * Болезни по ICD {@link Icd}
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "icd_id", referencedColumnName = "id")
    private Icd icd;

    /**
     * Комментарий
     */
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(name = "comment", length = 400)
    private String comment;

    /**
     * Список исследований {@link List<Examination>}
     */
    @NotNull(message = "Examinations can't be null")
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Examination> examinations;

    public Patient(String secondName, String firstName, String patronymic, LocalDateTime birthday, Icd icd, String comment) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.comment = comment;
        this.icd = icd;
        examinations = new ArrayList<>();
    }

    public void addExamination(Examination examination) {
        examinations.add(examination);
        examination.setPatient(this);
    }

    public void removeExamination(Examination examination) {
        examinations.remove(examination);
        examination.setPatient(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient that = (Patient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Patient o) {
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
        String icd_id = "-";

        if (icd != null) {
            icd_id = String.valueOf(icd.getId());
        }

        return "PatientRecord{" +
                "id=" + id +
                ", second_name='" + secondName + '\'' +
                ", first_name='" + firstName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", birthday=" + birthday.format(formatter) +
                ", icd_id=" + icd_id +
                '}';
    }
}
