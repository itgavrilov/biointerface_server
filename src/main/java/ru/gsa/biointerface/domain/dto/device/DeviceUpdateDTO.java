package ru.gsa.biointerface.domain.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * DTO (Data Transfer Object) контроллера биоинтерфейса для обновления
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "DeviceSaveOrUpdateDTO", description = "Biointerface controller for update")
public class DeviceUpdateDTO implements Serializable {

    static final long SerialVersionUID = 1L;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    protected String comment;

    @Override
    public String toString() {
        return "DeviceSaveOrUpdateDTO{" +
                "comment=" + comment +
                '}';
    }
}
