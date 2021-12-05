package ru.gsa.biointerface.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "device")
@Table(name = "device")
public class Device implements Serializable, Comparable<Device> {
    static final long SerialVersionUID = 1L;

    @NotNull(message = "Id can't be null")
    @Min(value = 1, message = "Id can't be lass then 1")
    @Id
    private int id;

    @NotNull(message = "Amount channels can't be null")
    @Min(value = 1, message = "Amount channels can't be lass then 1")
    @Max(value = 8, message = "Amount channels can't be more than 8")
    @Column(name = "amount_channels", nullable = false)
    private int amountChannels;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Examinations can't be null")
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private Set<Examination> examinations;

    public Device(int id, int amountChannels) {
        this.id = id;
        this.amountChannels = amountChannels;
        examinations = new TreeSet<>();
    }

    public void addExamination(Examination examination) {
        examinations.add(examination);
        examination.setDevice(this);
    }

    public void removeExamination(Examination examination) {
        examinations.remove(examination);
        examination.setDevice(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return id == device.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Device o) {
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
        return "Device{" +
                "id=" + id +
                ", amountChannels=" + amountChannels +
                '}';
    }
}
