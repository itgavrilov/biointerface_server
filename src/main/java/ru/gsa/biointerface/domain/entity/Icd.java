package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Сущность заболевания по международной классификации болезней (ICD)
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "icd")
@Table(name = "icd")
public class Icd implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор ICD
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    /**
     * Наименование заболевания по ICD
     */
    @NotBlank(message = "Name can't be blank")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Версия ICD
     */
    @NotNull(message = "Version can't be null")
    @Min(value = 10, message = "Version can't be lass then 10")
    @Max(value = 99, message = "Version can't be more than 99")
    @Column(name = "version", nullable = false)
    private Integer version;

    /**
     * Комментарий
     */
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    /**
     * Список карточек пациентов с этим заболеванием {@link Set<Patient>}
     */
    @OneToMany(mappedBy = "icd", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Patient> patients;

    public Icd(String name, int version, String comment) {
        this.name = name;
        this.version = version;
        this.comment = comment;
        patients = new ArrayList<>();
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        patient.setIcd(this);
    }

    public void removePatient(Patient patient) {
        patients.remove(patient);
        patient.setIcd(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Icd that = (Icd) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        Icd that = (Icd) o;

        return name.compareTo(that.name);
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
