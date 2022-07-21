package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO (Data Transfer Object) наименования канала контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Data
@Builder
@Schema(name = "ChannelName", description = "name for controller`s channel")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChannelNameDTO implements Serializable, Comparable<ChannelNameDTO> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Schema(description = "Channel`s name ID")
    private Integer id;

    /**
     * Наименование канала
     */
    @Schema(description = "Name", required = true)
    @NotBlank(message = "Name can't be blank")
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    private String name;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelNameDTO that = (ChannelNameDTO) o;
        return Objects.equals(id, that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ChannelNameDTO o) {
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
        return "ChannelName{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "comment='" + comment + '\'' +
                '}';
    }
}
