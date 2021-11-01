package ru.gsa.biointerface.repository.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.DeviceRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class DeviceRepositoryImpl extends AbstractRepository<Device, Long> implements DeviceRepository {
    @Autowired
    public DeviceRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
