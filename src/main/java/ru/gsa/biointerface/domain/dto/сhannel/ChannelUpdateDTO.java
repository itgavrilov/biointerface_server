package ru.gsa.biointerface.domain.dto.сhannel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameDTO;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) канала контроллера биоинтерфейса для обновления
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "ChannelUpdateDTO", description = "Controller`s channel")
public class ChannelUpdateDTO implements Serializable {

    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор наименования канала контроллера биоинтерфейса {@link ChannelNameDTO#getId()}
     */
    @Schema(description = "Channel`s name ID")
    protected UUID channelNameId;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    protected String comment;

    @Override
    public String toString() {

        return "ChannelUpdateDTO{" +
                "channelName_id=" + channelNameId +
                ", comment=" + comment +
                '}';
    }
}

