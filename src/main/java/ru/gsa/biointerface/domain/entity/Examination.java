package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность исследования
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "examination")
@Table(name = "examination")
public class Examination implements Serializable, Comparable<Object> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    /**
     * Время начала исследования
     */
    @PastOrPresent(message = "Start time should be in past or present")
    @Column(name = "starttime", nullable = false)
    private LocalDateTime datetime;

    /**
     * Карточка пациента
     */
    @NotNull(message = "Patient can't be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

    /**
     * Контроллер биоинтерфейса
     */
    @NotNull(message = "Device can't be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "device_id", referencedColumnName = "id", nullable = false)
    private Device device;

    /**
     * Комментарий
     */
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(name = "comment", length = 400)
    private String comment;

    /**
     * Список каналов контроллера биоинтерфейса {@link List<Channel>}
     */
    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Channel> channels;

    public Examination(Patient patient, Device device, String comment) {
        this.datetime = LocalDateTime.now();
        this.comment = comment;
        this.device = device;
        this.patient = patient;
        channels = new ArrayList<>();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        channel.setExamination(this);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
        channel.setExamination(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Examination that = (Examination) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        Examination that = (Examination) o;

        return datetime.compareTo(that.datetime);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyy-MM-dd hh:mm:ss");

        return "Examination{" +
                "id=" + id +
                ", datetime=" + formatter.format(datetime) +
                ", patientRecord_id=" + patient.getId() +
                ", device_id=" + device.getId() +
                '}';
    }
}
