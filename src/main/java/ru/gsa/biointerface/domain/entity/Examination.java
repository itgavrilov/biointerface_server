package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Builder(toBuilder = true)
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
    @NotNull(message = "Patient's ID can't be null")
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    /**
     * Контроллер биоинтерфейса
     */
    @NotNull(message = "Device's ID can't be null")
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    /**
     * Комментарий
     */
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(name = "comment", length = 400)
    private String comment;

    /**
     * Дата создания
     */
    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * Дата последнего изменений
     */
    @UpdateTimestamp
    @Column(name = "modify_date", nullable = false)
    private LocalDateTime modifyDate;

    /**
     * Список каналов контроллера биоинтерфейса {@link List<Channel>}
     */
    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Channel> channels;

    public void setChannels(List<Channel> channels) {
        this.channels = channels;

        if (this.channels != null) {
            this.channels.forEach(c -> c.setExamination(this));
        }
    }

    public Examination(UUID patientId, UUID deviceId, String comment) {
        this.datetime = LocalDateTime.now();
        this.comment = comment;
        this.patientId = patientId;
        this.deviceId = deviceId;
        channels = new ArrayList<>();
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
                ", patientRecord_id=" + patientId +
                ", device_id=" + deviceId +
                '}';
    }
}
