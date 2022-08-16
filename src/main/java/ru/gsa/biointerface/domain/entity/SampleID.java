package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Objects;

/**
 * Сущность идентификатора измерения
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class SampleID implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Порядковый номер
     */
    @Min(value = 0, message = "Number can't be lass then 0")
    @Column(name = "number")
    private long number;

    /**
     * Идентификатор канала контроллера биоинтерфейса {@link ChannelID}
     */
    @Embedded
    private ChannelID channelId;

    public SampleID(Integer number, ChannelID channelId) {
        this.number = number;
        this.channelId = channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleID that = (SampleID) o;

        return number == that.number && Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, channelId);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        SampleID that = (SampleID) o;

        int result = channelId.compareTo(that.channelId);
        if (result == 0) {
            if (number > that.number) {
                result = 1;
            } else if (number < that.number) {
                result = -1;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        String channelId = "-";
        String examinationId = "-";

        if (this.channelId != null) {
            channelId = String.valueOf(this.channelId.getNumber());
            examinationId = String.valueOf(this.channelId.getExaminationId());
        }
        return "Sample{" +
                "number=" + number +
                ", channel_id=" + channelId +
                ", examination_id=" + examinationId +
                '}';
    }
}
