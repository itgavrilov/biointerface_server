package ru.gsa.biointerface.domain.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDTO implements Serializable, Comparable<ChannelDTO> {
    @NotNull(message = "Number can't be null")
    private int number;

    @NotNull(message = "Examination id can't be null")
    private int examination_id;

    private int channelName_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelDTO that = (ChannelDTO) o;
        return number == that.number && examination_id == that.examination_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, examination_id);
    }

    @Override
    public int compareTo(ChannelDTO o) {
        int result = examination_id - o.examination_id;

        if (result == 0)
            result = number - o.number;

        return result;
    }

    @Override
    public String toString() {

        return "Channel{" +
                "number=" + number +
                ", examination_id=" + examination_id +
                ", channelName_id=" + channelName_id +
                '}';
    }
}

