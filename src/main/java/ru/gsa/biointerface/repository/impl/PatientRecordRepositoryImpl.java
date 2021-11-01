package ru.gsa.biointerface.repository.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.PatientRecordRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class PatientRecordRepositoryImpl extends AbstractRepository<PatientRecord, Long> implements PatientRecordRepository {
    @Autowired
    public PatientRecordRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
