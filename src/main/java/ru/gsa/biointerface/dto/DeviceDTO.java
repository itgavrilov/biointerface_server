package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Data
@Builder
@Schema(name = "Device", description = "biointerface controller")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class DeviceDTO implements Serializable, Comparable<DeviceDTO> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Device ID")
    private Integer id;

    /**
     * Количество каналов
     */
    @Schema(description = "device Amount channels", required = true)
    @Min(value = 1, message = "Amount channels can't be lass then 1")
    @Max(value = 8, message = "Amount channels can't be more than 8")
    private Integer amountChannels;

    /**
     * Комментарий
     */
    @Schema(description = "device comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return Objects.equals(id, deviceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(DeviceDTO o) {
        if (o == null || getClass() != o.getClass()) return -1;
        int result = 0;

        if (id > o.id) {
            result = 1;
        } else if (id < o.id) {
            result = -1;
        }

        return result;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", amountChannels=" + amountChannels +
                '}';
    }
}
