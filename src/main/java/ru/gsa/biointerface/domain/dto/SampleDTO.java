package ru.gsa.biointerface.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) измерения
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Builder
@Data
@Schema(name = "Sample", description = "reading of examination")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SampleDTO implements Serializable, Comparable<SampleDTO> {
    static final long SerialVersionUID = 1L;

    /**
     * Порядковый номер измерения
     */
    @Schema(description = "sample serial number in examination")
    @Min(value = 0, message = "Id can't be lass then 0")
    private Integer id;

    /**
     * Номер канала контроллера биоинтерфейса {@link ChannelDTO#getNumber()}
     */
    @Schema(description = "channel serial number in controller")
    @NotNull(message = "ChannelNumber can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    private Integer channelNumber;

    /**
     * Номер канала контроллера биоинтерфейса {@link ChannelDTO#getExaminationId()}
     */
    @Schema(description = "examination ID")
    @NotNull(message = "ExaminationId can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    private Integer examinationId;

    /**
     * Значение
     */
    @Schema(description = "sample value")
    @NotNull(message = "Value can't be null")
    private Integer value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleDTO sampleDTO = (SampleDTO) o;
        return Objects.equals(id, sampleDTO.id)
                && Objects.equals(channelNumber, sampleDTO.channelNumber)
                && Objects.equals(examinationId, sampleDTO.examinationId);
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
