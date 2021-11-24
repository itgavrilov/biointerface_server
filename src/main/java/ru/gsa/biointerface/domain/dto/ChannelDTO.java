package ru.gsa.biointerface.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChannelDTO implements Serializable, Comparable<ChannelDTO> {
    @NotNull(message = "Number can't be null")
    private int number;

    @NotNull(message = "ExaminationId can't be null")
    private int examinationId;

    private int channelNameId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelDTO that = (ChannelDTO) o;
        return number == that.number && examinationId == that.examinationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, examinationId);
    }

    @Override
    public int compareTo(ChannelDTO o) {
        int result = examinationId - o.examinationId;

        if (result == 0)
            result = number - o.number;

        return result;
    }

    @Override
    public String toString() {

        return "Channel{" +
                "number=" + number +
                ", examination_id=" + examinationId +
                ", channelName_id=" + channelNameId +
                '}';
    }
}

