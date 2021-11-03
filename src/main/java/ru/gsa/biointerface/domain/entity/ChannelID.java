package ru.gsa.biointerface.domain.entity;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Embeddable
public class ChannelID implements Serializable, Comparable<ChannelID> {
    @NotNull(message = "Id can't be null")
    private Integer id;

    @NotNull(message = "Id can't be null")
    private Long examination_id;

    public ChannelID() {
    }

    public ChannelID(Integer id, Long examination_id) {
        this.id = id;
        this.examination_id = examination_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getExamination_id() {
        return examination_id;
    }

    public void setExamination_id(Long examination_id) {
        this.examination_id = examination_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelID that = (ChannelID) o;
        return Objects.equals(id, that.id) && Objects.equals(examination_id, that.examination_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, examination_id);
    }

    @Override
    public int compareTo(ChannelID o) {
        int result = examination_id.compareTo(o.examination_id);

        if (result == 0)
            result = id - o.id;

        return result;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "number=" + id +
                ", examinationEntity_id=" + examination_id +
                '}';
    }
}
