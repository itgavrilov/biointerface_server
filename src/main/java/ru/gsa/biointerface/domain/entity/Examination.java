package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class Examination implements Serializable, Comparable<Examination> {
    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Время начала исследования
     */
    @PastOrPresent(message = "Start time should be in past or present")
    @Column(name = "starttime", nullable = false)
    private LocalDateTime starttime;

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
        this.starttime = LocalDateTime.now();
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
        Examination entity = (Examination) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Examination o) {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyy-MM-dd hh:mm:ss");

        return "Examination{" +
                "id=" + id +
                ", datetime=" + formatter.format(starttime) +
                ", patientRecord_id=" + patient.getId() +
                ", device_id=" + device.getId() +
                '}';
    }
}
