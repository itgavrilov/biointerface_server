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
public class SampleID implements Serializable, Comparable<SampleID> {
    static final long SerialVersionUID = 1L;

    /**
     * Порядковый номер
     */
    @Min(value = 0, message = "Number can't be lass then 0")
    @Column(name = "number")
    private int number;

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
        SampleID sampleID = (SampleID) o;
        return number == sampleID.number && Objects.equals(channel_id, sampleID.channel_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, channel_id);
    }

    @Override
    public int compareTo(SampleID o) {
        if (o == null || getClass() != o.getClass()) return -1;
        int result = channel_id.compareTo(o.channel_id);

        if (result == 0) {
            if (number > o.number) {
                result = 1;
            } else if (number < o.number) {
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
