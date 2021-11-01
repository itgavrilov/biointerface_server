package ru.gsa.biointerface.repository.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Icd;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class IcdRepositoryImpl extends AbstractRepository<Icd, Long> {
    @Autowired
    public IcdRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
