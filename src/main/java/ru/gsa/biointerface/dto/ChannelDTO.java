package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Builder
@Data
@Schema(name = "Channel", description = "controller`s channel")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChannelDTO implements Serializable, Comparable<ChannelDTO> {
    static final long SerialVersionUID = 1L;

    @Schema(description = "serial number in controller")
    @NotNull(message = "Number can't be null")
    private int number;

    @Schema(description = "examination`s ID")
    @NotNull(message = "ExaminationId can't be null")
    private int examinationId;

    @Schema(description = "channel`s name ID")
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
                ", examination_id=" + examinationId +
                ", channelName_id=" + channelNameId +
                '}';
    }
}

