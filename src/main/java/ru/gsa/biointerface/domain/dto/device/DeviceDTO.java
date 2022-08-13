package ru.gsa.biointerface.domain.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "DeviceDTO", description = "Biointerface controller")
public class DeviceDTO extends DeviceUpdateDTO implements Serializable, Comparable<Object> {

    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Device ID")
    private UUID id;

    /**
     * Серийный номер
     */
    @NotBlank(message = "Number can't be blank")
    private String number;

    /**
     * Количество каналов
     */
    @Schema(description = "device Amount channels", required = true)
    @Min(value = 1, message = "Amount channels can't be lass then 1")
    @Max(value = 8, message = "Amount channels can't be more than 8")
    private Integer amountChannels;

    /**
     * Дата создания
     */
    @Schema(description = "Creation date")
    private LocalDateTime creationDate;

    /**
     * Дата последнего изменений
     */
    @Schema(description = "Modify date")
    private LocalDateTime modifyDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO that = (DeviceDTO) o;

        return Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        DeviceDTO that = (DeviceDTO) o;

        return number.compareTo(that.number);
    }

    @Override
    public String toString() {
        return "DeviceDTO{" +
                "id=" + id +
                ", number=" + number +
                ", amountChannels=" + amountChannels +
                '}';
    }
}
