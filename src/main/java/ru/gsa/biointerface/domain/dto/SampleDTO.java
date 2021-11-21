package ru.gsa.biointerface.domain.dto;

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
public class SampleDTO implements Serializable, Comparable<SampleDTO> {
    @NotNull(message = "Id can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    private int id;

    @NotNull(message = "Channel number can't be null")
    private int channel_number;

    @NotNull(message = "Examination id can't be null")
    private int examination_id;

    @NotNull(message = "Value can't be null")
    private int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleDTO sampleDTO = (SampleDTO) o;
        return id == sampleDTO.id
                && channel_number == sampleDTO.channel_number
                && examination_id == sampleDTO.examination_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channel_number, examination_id);
    }

    @Override
    public int compareTo(SampleDTO o) {
        int result = examination_id - o.examination_id;

        if (result == 0)
            result = channel_number - o.channel_number;

        if (result == 0)
            result = id = o.id;

        return result;
    }

    @Override
    public String toString() {
        return "SampleDTO{" +
                "id=" + id +
                ", channel_number=" + channel_number +
                ", examination_id=" + examination_id +
                ", value=" + value +
                '}';
    }
}
