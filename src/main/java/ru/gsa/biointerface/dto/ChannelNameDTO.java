package ru.gsa.biointerface.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Builder
@Data
@Schema(name = "ChannelName", description = "name for controller`s channel")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChannelNameDTO implements Serializable, Comparable<ChannelNameDTO> {
    static final long SerialVersionUID = 1L;

    @Schema(description = "channel`s name ID")
    @NotNull(message = "Id can't be null")
    private int id;

    @Schema(description = "channel`s name")
    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blank")
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    private String name;

    @Schema(description = "channel`s name comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelNameDTO that = (ChannelNameDTO) o;
        return id == that.id && name.equals(that.name);
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
