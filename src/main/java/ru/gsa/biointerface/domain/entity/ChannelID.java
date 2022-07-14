package ru.gsa.biointerface.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ChannelID implements Serializable, Comparable<ChannelID> {
    static final long SerialVersionUID = 1L;

    @NotNull(message = "Examination id can't be null")
    @Column(name = "examination_id")
    private int examinationId;

    @NotNull(message = "Number can't be null")
    @Column(name = "number")
    private int number;

    public ChannelID(int examinationId, int number) {
        this.examinationId = examinationId;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelID channelID = (ChannelID) o;
        return examinationId == channelID.examinationId && number == channelID.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(examinationId, number);
    }

    @Override
    public int compareTo(ChannelID o) {
        if (o == null || getClass() != o.getClass()) return -1;
        int result = examinationId - o.examinationId;

        if (result == 0)
            result = number - o.number;

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
