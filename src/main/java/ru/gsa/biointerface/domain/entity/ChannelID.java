package ru.gsa.biointerface.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность идентификатора канала контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ChannelID implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор исследования {@link Examination}
     */
    @NotNull(message = "Examination id can't be null")
    @Column(name = "examination_id")
    private UUID examinationId;

    /**
     * Порядковый номер
     */
    @NotNull(message = "Number can't be null")
    @Min(value = 0, message = "Number can't be lass then 0")
    @Column(name = "number", nullable = false)
    private Byte number;

    public ChannelID(UUID examinationId, byte number) {
        this.examinationId = examinationId;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelID that = (ChannelID) o;
        return Objects.equals(examinationId, that.examinationId) && Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(examinationId, number);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        ChannelID that = (ChannelID) o;

        int result = examinationId.compareTo(that.examinationId);
        if (result == 0) result = number - that.number;

        return result;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "number=" + number +
                ", examinationEntity_id=" + examinationId +
                '}';
    }
}
