package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, ChannelID> {

    default Optional<Channel> getByNumberAndExaminationId(Integer number, Integer examinationId){
        ChannelID id = new ChannelID(number, examinationId);

        return findById(id);
    }

    List<Channel> findAllByExamination(Examination examination);

    List<Channel> findAllByChannelName(ChannelName channelName);
}
