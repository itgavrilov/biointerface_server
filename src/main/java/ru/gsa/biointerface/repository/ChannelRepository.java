package ru.gsa.biointerface.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, ChannelID> {

    String MASK_NOT_FOUND = "Channel(id=%s) is not found";

    default Optional<Channel> getByNumberAndExaminationId(Integer examinationId, Integer number) {
        ChannelID id = new ChannelID(number, examinationId);

        return findById(id);
    }

    default Channel getOrThrow(Integer examinationId, Integer number) {
        ChannelID id = new ChannelID(number, examinationId);
        return getOrThrow(id);
    }

    default Channel getOrThrow(ChannelID id) {
        return findById(id).orElseThrow(() -> new NotFoundException(String.format(
                MASK_NOT_FOUND, id)));
    }

    @Query(nativeQuery = true,
            value = "select * from channel as c " +
                    "where (:examinationId is null or c.examination_id = :examinationId) " +
                    "and (:channelNameId is null or c.channel_name_id = :channelNameId) ")
    List<Channel> findAllByExaminationIdAndChannelNameId(@Param("examinationId") Integer examinationId,
                                                         @Param("channelNameId") Integer channelNameId);


    @Query(nativeQuery = true,
            value = "select * from channel as c " +
                    "where (:examinationId is null or c.examination_id = :examinationId) " +
                    "and (:channelNameId is null or c.channel_name_id = :channelNameId) ")
    Page<Channel> findAllByExaminationIdAndChannelNameId(@Param("examinationId") Integer examinationId,
                                                         @Param("channelNameId") Integer channelNameId,
                                                         Pageable pageable);
}
