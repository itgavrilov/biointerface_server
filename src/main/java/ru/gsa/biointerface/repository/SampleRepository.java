package ru.gsa.biointerface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.Sample;
import ru.gsa.biointerface.domain.SampleID;
import ru.gsa.biointerface.repository.customized.SampleRepositoryCustom;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
@Repository
public interface SampleRepository extends JpaRepository<Sample, SampleID>, SampleRepositoryCustom {
    List<Sample> findAllByChannel(Channel channel);
}
