package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.database.DataSource;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameRepository extends AbstractRepository<ChannelName, Long> {
    public ChannelNameRepository(DataSource dataSource) {
        super(dataSource);
    }
}
