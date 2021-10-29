package ru.gsa.biointerface.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.database.DataSource;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class IcdRepository extends AbstractRepository<Icd, Long> {
    @Autowired
    public IcdRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
