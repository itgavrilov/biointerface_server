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
import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, ChannelID> {

    String MASK_NOT_FOUND = "Channel(examinationId=%s, number=%s) is not found";
    String MASK_NOT_FOUND_BY_ID = "Channel(id=%s) is not found";

    default Channel getOrThrow(UUID examinationId, Byte number) {
        if (examinationId == null || number == null) {
            throw new NotFoundException(String.format(MASK_NOT_FOUND, examinationId, number));
        }

        ChannelID id = new ChannelID(examinationId, number);
        return getOrThrow(id);
    }

    default Channel getOrThrow(ChannelID id) {
        if (id == null || id.getExaminationId() == null || id.getNumber() == null) {
            throw new NotFoundException(String.format(MASK_NOT_FOUND_BY_ID, id));
        }

        return findById(id).orElseThrow(() -> new NotFoundException(String.format(
                MASK_NOT_FOUND_BY_ID, id)));
    }

    @Query(nativeQuery = true,
            value = "select * from main_service.channel as c " +
                    "where (:examinationId is null or c.examination_id = :examinationId) " +
                    "and (:channelNameId is null or c.channel_name_id = :channelNameId) ")
    List<Channel> findAllByExaminationIdAndChannelNameId(@Param("examinationId") UUID examinationId,
                                                         @Param("channelNameId") UUID channelNameId);


    @Query(nativeQuery = true,
            value = "select * from main_service.channel as c " +
                    "where (:examinationId is null or c.examination_id = :examinationId) " +
                    "and (:channelNameId is null or c.channel_name_id = :channelNameId) ")
    Page<Channel> findAllByExaminationIdAndChannelNameId(@Param("examinationId") UUID examinationId,
                                                         @Param("channelNameId") UUID channelNameId,
                                                         Pageable pageable);
}
