package ru.gsa.biointerface.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
@NoArgsConstructor
@Entity(name = "channel_name")
@Table(name = "channel_name")
public class ChannelName implements Serializable, Comparable<ChannelName> {
    @NotNull(message = "Id can't be null")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blank")
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    @Column(length = 35, unique = true, nullable = false)
    private String name;

    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    @NotNull(message = "Channels can't be null")
    @OneToMany(mappedBy = "channelName", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Channel> channels;

    public ChannelName(String name, String comment) {
        this.id = -1;
        this.name = name;
        this.comment = comment;
        this.channels = new TreeSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelName that = (ChannelName) o;
        return id == that.id && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ChannelName o) {
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
