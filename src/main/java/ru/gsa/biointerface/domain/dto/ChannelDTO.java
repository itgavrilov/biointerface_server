package ru.gsa.biointerface.domain.dto;

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
 * DTO (Data Transfer Object) канала контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Data
@Builder
@Schema(name = "Channel", description = "controller`s channel")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChannelDTO implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор исследования {@link ExaminationDTO#getId()}
     */
    @Schema(description = "examination`s ID", required = true)
    @NotNull(message = "Examination id can't be null")
    private UUID examinationId;

    /**
     * Номер
     */
    @Schema(description = "serial number in controller", required = true)
    @Min(value = 0, message = "Number can't be lass then 0")
    private Byte number;

    /**
     * Идентификатор наименования канала контроллера биоинтерфейса {@link ChannelNameDTO#getId()}
     */
    @Schema(description = "channel`s name ID")
    private UUID channelNameId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelDTO that = (ChannelDTO) o;
        return Objects.equals(number, that.number) && Objects.equals(examinationId, that.examinationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, examinationId);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        ChannelDTO that = (ChannelDTO) o;

        int result = examinationId.compareTo(that.examinationId);
        if (result == 0) result = number - that.number;

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

