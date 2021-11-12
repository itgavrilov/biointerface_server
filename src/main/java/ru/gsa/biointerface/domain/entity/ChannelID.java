package ru.gsa.biointerface.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ChannelID implements Serializable, Comparable<ChannelID> {
    @NotNull(message = "Id can't be null")
    private int number;

    @NotNull(message = "Id can't be null")
    private int examination_id;

    public ChannelID(int id, int examination_id) {
        this.number = id;
        this.examination_id = examination_id;
    }

    @Override
    public int compareTo(ChannelID o) {
        int result = examination_id - o.examination_id;

        if (result == 0)
            result = number - o.number;

        return result;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "number=" + number +
                ", examinationEntity_id=" + examination_id +
                '}';
    }
}
