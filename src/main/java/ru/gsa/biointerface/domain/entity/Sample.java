package ru.gsa.biointerface.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Сущность измерения
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "sample")
@Table(name = "sample")
public class Sample implements Serializable, Comparable<Sample> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор {@link SampleID}
     */
    @NotNull(message = "Id can't be null")
    @Min(value = 0, message = "Id can't be lass then 0")
    @EmbeddedId
    private SampleID id;

    /**
     * Канал контроллера биоинтерфейса {@link Channel}
     */
    @NotNull(message = "Channel can't be null")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumns({
            @JoinColumn(name = "examination_id", referencedColumnName = "examination_id", nullable = false),
            @JoinColumn(name = "channel_number", referencedColumnName = "number", nullable = false)
    })
    @MapsId("channel_id")
    private Channel channel;

    /**
     * Значение
     */
    @NotNull(message = "Value can't be null")
    @Column(nullable = false)
    private int value;

    public Sample(int id, Channel channel, int value) {
        this.id = new SampleID(id, channel.getId());
        this.channel = channel;
        channel.getSamples().add(this);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sample that = (Sample) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Sample o) {
        return id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
