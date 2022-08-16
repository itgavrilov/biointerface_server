package ru.gsa.biointerface.domain.dto.channelName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) наименования канала контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "ChannelNameDTO", description = "Name for controller`s channel")
public class ChannelNameDTO extends ChannelNameSaveOrUpdateDTO implements Serializable, Comparable<Object> {

    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Channel`s name ID")
    private UUID id;

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
        ChannelNameSaveOrUpdateDTO that = (ChannelNameSaveOrUpdateDTO) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        ChannelNameSaveOrUpdateDTO that = (ChannelNameSaveOrUpdateDTO) o;

        return name.compareTo(that.name);
    }

    @Override
    public String toString() {
        return "ChannelNameDTO{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "comment='" + comment + '\'' +
                '}';
    }
}
