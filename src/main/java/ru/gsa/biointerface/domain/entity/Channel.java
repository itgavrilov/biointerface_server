package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Сущность канала контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "channel")
@Table(name = "channel")
public class Channel implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор {@link ChannelID}
     */
    @EmbeddedId
    ChannelID id;

    /**
     * Исследование {@link Examination}
     */
    @NotNull(message = "Examination can't be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "examination_id", referencedColumnName = "id", nullable = false)
    @MapsId("examinationId")
    private Examination examination;

    /**
     * Наименование канала контроллера биоинтерфейса {@link ChannelName}
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "channel_name_id", referencedColumnName = "id")
    private ChannelName channelName;

    /**
     * Массив измерений {@link List<Sample>}
     */
    @NotNull(message = "Samples can't be null")
    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Sample> samples;

    public Channel(Byte number, Examination examination, ChannelName channelName) {
        this.id = new ChannelID(examination.getId(), number);
        this.examination = examination;
        this.channelName = channelName;
        this.samples = new LinkedList<>();
    }

    public void addSample(Sample sample) {
        sample.setId(new SampleID(samples.size(), id));
        samples.add(sample);
        sample.setChannel(this);
    }

    public void removeSample(Sample sample) {
        samples.remove(sample);
        sample.setChannel(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel that = (Channel) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        Channel that = (Channel) o;

        return id.compareTo(that.id);
    }

    @Override
    public String toString() {
        String channelNameId = "-";

        if (channelName != null)
            channelNameId = String.valueOf(channelName.getId());

        return "Channel{" +
                "number=" + id.getNumber() +
                ", examination_id=" + id.getExaminationId() +
                ", channelName_id=" + channelNameId +
                '}';
    }
}

