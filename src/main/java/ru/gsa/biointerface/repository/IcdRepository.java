package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.database.DataSource;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdRepository extends AbstractRepository<Icd, Long> {
    public IcdRepository(DataSource dataSource) {
        super(dataSource);
    }
}
