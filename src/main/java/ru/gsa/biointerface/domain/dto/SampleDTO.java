package ru.gsa.biointerface.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Min;
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
public class SampleDTO implements Serializable, Comparable<SampleDTO> {
    @NotNull(message = "Id can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    private int id;

    @NotNull(message = "ChannelNumber can't be null")
    private int channelNumber;

    @NotNull(message = "ExaminationId can't be null")
    private int examinationId;

    @NotNull(message = "Value can't be null")
    private int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleDTO sampleDTO = (SampleDTO) o;
        return id == sampleDTO.id
                && channelNumber == sampleDTO.channelNumber
                && examinationId == sampleDTO.examinationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channelNumber, examinationId);
    }

    @Override
    public int compareTo(SampleDTO o) {
        if (o == null || getClass() != o.getClass()) return -1;
        int result = examinationId - o.examinationId;

        if (result == 0)
            result = channelNumber - o.channelNumber;

        if (result == 0)
            result = id = o.id;

        return result;
    }

    @Override
    public String toString() {
        return "SampleDTO{" +
                "id=" + id +
                ", channel_number=" + channelNumber +
                ", examination_id=" + examinationId +
                ", value=" + value +
                '}';
    }
}
