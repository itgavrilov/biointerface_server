package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.ChannelID;
import ru.gsa.biointerface.domain.ChannelName;
import ru.gsa.biointerface.domain.Examination;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, ChannelID> {
    List<Channel> findAllByExamination(Examination examination);

    List<Channel> findAllByChannelName(ChannelName channelName);
}
