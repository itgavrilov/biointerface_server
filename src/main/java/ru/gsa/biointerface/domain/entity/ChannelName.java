package ru.gsa.biointerface.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность наименования канала контроллера биоинтерфейса
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "channel_name")
@Table(name = "channel_name")
public class ChannelName implements Serializable, Comparable<Object> {
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
     * Наименование канала
     */
    @NotNull(message = "Name can't be null")
    @NotBlank(message = "Name can't be blank")
    @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
    @Column(length = 35, unique = true, nullable = false)
    private String name;

    /**
     * Комментарий
     */
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    @Column(length = 400)
    private String comment;

    /**
     * Список каналов с этим наименованием {@link List<Channel>}
     */
    @NotNull(message = "Channels can't be null")
    @OneToMany(mappedBy = "channelName", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Channel> channels;

    public ChannelName(String name, String comment) {
        this.name = name;
        this.comment = comment;
        channels = new ArrayList<>();
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
        channel.setChannelName(this);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
        channel.setChannelName(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelName that = (ChannelName) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || getClass() != o.getClass()) return -1;
        ChannelName that = (ChannelName) o;

        return name.compareTo(that.name);
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
