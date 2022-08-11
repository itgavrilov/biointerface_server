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
import javax.persistence.Index;
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
import java.util.UUID;

/**
 * Сущность контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "device")
@Table(name = "device", indexes = {
        @Index(name = "ix_device_number", columnList = "number")
})
public class Device implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    /**
     * Серийный номер
     */
    @NotBlank(message = "Number can't be blank")
    @Column(name = "number", nullable = false, unique = true)
    private String number;

    /**
     * Количество каналов
     */
    @NotNull(message = "AmountChannels can't be null")
    @Min(value = 1, message = "Amount channels can't be lass then 1")
    @Max(value = 8, message = "Amount channels can't be more than 8")
    @Column(name = "amount_channels", nullable = false)
    private Integer amountChannels;

    /**
     * Комментарий
     */
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    /**
     * Список исследований {@link List<Examination>}
     */
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<Examination> examinations;

    public Device(String number, int amountChannels, String comment) {
        this.number = number;
        this.amountChannels = amountChannels;
        this.comment = comment;
        examinations = new ArrayList<>();
    }

    public void addExamination(Examination examination) {
        examinations.add(examination);
        examination.setDevice(this);
    }

    public void removeExamination(Examination examination) {
        examinations.remove(examination);
        examination.setDevice(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device that = (Device) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        Device that = (Device) o;

        return number.compareTo(that.number);
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", amountChannels=" + amountChannels +
                '}';
    }
}
