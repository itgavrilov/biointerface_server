package ru.gsa.biointerface.domain.entity;

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
    private ChannelID channel_id;

    public SampleID(Integer number, ChannelID channel_id) {
        this.number = number;
        this.channel_id = channel_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleID that = (SampleID) o;

        return number == that.number && Objects.equals(channel_id, that.channel_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, channel_id);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        SampleID that = (SampleID) o;

        int result = channel_id.compareTo(that.channel_id);
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

        if (channel_id != null) {
            channelId = String.valueOf(channel_id.getNumber());
            examinationId = String.valueOf(channel_id.getExaminationId());
        }
        return "Sample{" +
                "number=" + number +
                ", channel_id=" + channelId +
                ", examination_id=" + examinationId +
                '}';
    }
}
