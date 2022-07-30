package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) измерения
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Builder
@Data
@Schema(name = "Sample", description = "reading of examination")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SampleDTO implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Порядковый номер измерения
     */
    @Schema(description = "sample serial number in examination", required = true)
    private Long number;

    /**
     * Номер канала контроллера биоинтерфейса {@link ChannelDTO#getNumber()}
     */
    @Schema(description = "channel serial number in controller", required = true)
    @NotNull(message = "ChannelNumber can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    private Byte channelNumber;

    /**
     * Номер канала контроллера биоинтерфейса {@link ChannelDTO#getExaminationId()}
     */
    @Schema(description = "examination ID", required = true)
    @NotNull(message = "ExaminationId can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    private UUID examinationId;

    /**
     * Значение
     */
    @Schema(description = "sample value", required = true)
    @NotNull(message = "Value can't be null")
    private Integer value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleDTO that = (SampleDTO) o;

        return Objects.equals(number, that.number)
                && Objects.equals(channelNumber, that.channelNumber)
                && Objects.equals(examinationId, that.examinationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, channelNumber, examinationId);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        SampleDTO that = (SampleDTO) o;

        int result = examinationId.compareTo(that.examinationId);
        if (result == 0) result = channelNumber - that.channelNumber;
        if (result == 0) result = number.compareTo(that.number);

        return result;
    }

    @Override
    public String toString() {
        return "SampleDTO{" +
                "id=" + number +
                ", channel_number=" + channelNumber +
                ", examination_id=" + examinationId +
                ", value=" + value +
                '}';
    }
}
