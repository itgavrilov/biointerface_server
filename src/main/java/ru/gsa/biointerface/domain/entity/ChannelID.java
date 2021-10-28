package ru.gsa.biointerface.domain.entity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelID implements Serializable, Comparable<ChannelID> {
    @NotNull
    private int id;

    @NotNull
    private Examination examination;

    public ChannelID() {
    }

    public ChannelID(int id, Examination examination) {
        this.id = id;
        this.examination = examination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelID that = (ChannelID) o;
        return id == that.id && Objects.equals(examination, that.examination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, examination);
    }

    @Override
    public int compareTo(ChannelID o) {
        int result = examination.compareTo(o.examination);

        if (result == 0)
            result = id - o.id;

        return result;
    }

    @Override
    public String toString() {
        return "ChannelID{" +
                "number=" + id +
                ", examination=" + examination +
                '}';
    }
}
