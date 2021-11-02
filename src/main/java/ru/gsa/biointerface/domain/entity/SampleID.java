package ru.gsa.biointerface.domain.entity;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SampleID implements Serializable, Comparable<SampleID> {
    @NotNull(message = "Id can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    @Id
    private long id;

    @NotNull(message = "Channel can't be null")
    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "examination_id", referencedColumnName = "examination_id", nullable = false),
            @JoinColumn(name = "channel_id", referencedColumnName = "id", nullable = false)
    })
    private Channel channel;

    public SampleID() {
    }

    public SampleID(long id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    public SampleID getPK() {
        return new SampleID(id, channel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleID that = (SampleID) o;
        return id == that.id && Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channel);
    }

    @Override
    public int compareTo(SampleID o) {
        int result = channel.compareTo(o.channel);

        if (result == 0) {
            if (id > o.id) {
                result = 1;
            } else if (id < o.id) {
                result = -1;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        String channelId = "-";
        String examinationId = "-";

        if (channel != null) {
            channelId = String.valueOf(channel.getId());

            if (channel.getExamination() != null)
                examinationId = String.valueOf(channel.getExamination().getId());
        }
        return "Sample{" +
                "id=" + id +
                ", examination_id=" + channelId +
                ", numberOfChannel=" + examinationId +
                '}';
    }
}
