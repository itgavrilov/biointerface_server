package ru.gsa.biointerface.repository.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class ChannelNameRepositoryImpl extends AbstractRepository<ChannelName, Long> implements ChannelNameRepository {
    @Autowired
    public ChannelNameRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
