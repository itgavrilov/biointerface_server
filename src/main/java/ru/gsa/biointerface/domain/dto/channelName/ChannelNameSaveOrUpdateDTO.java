package ru.gsa.biointerface.domain.dto.channelName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * DTO (Data Transfer Object) наименования канала контроллера биоинтерфейса для создания или обновления
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "ChannelNameSaveOrUpdateDTO", description = "Name for controller`s channel for save or update")
public class ChannelNameSaveOrUpdateDTO implements Serializable {

    static final long SerialVersionUID = 1L;

    /**
     * Наименование канала
     */
    @Schema(description = "Name", required = true)
    @NotBlank(message = "Name can't be blank")
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    protected String name;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    protected String comment;

    @Override
    public String toString() {
        return "ChannelNameSaveOrUpdateDTO{" +
                "name='" + name + '\'' +
                "comment='" + comment + '\'' +
                '}';
    }
}
