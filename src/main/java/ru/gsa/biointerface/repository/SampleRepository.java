package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.domain.entity.SampleID;
import ru.gsa.biointerface.repository.customized.SampleRepositoryCustom;

import java.util.List;
import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface SampleRepository extends JpaRepository<Sample, SampleID>, SampleRepositoryCustom {

    List<Sample> findAllByChannel(Channel channel);

    @Query(nativeQuery = true,
            value = "select * from main_service.sample as s " +
                    "where s.examination_id = :examinationId " +
                    "and s.channel_number = :channelNumber ")
    List<Sample> findAllByExaminationIdAndChannelNumber(@Param("examinationId") UUID examinationId,
                                                        @Param("channelNumber") byte channelNumber);
}
