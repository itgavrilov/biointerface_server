package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.DeviceRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository repository;

    @Autowired
    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("DeviceService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("DeviceService is destruction");
    }

    public List<Device> getAll() throws Exception {
        List<Device> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all devices from database");
        } else {
            LOGGER.info("Devices is not found in database");
        }

        return entities;
    }

    public Device findById(Long id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Device> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get device(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            LOGGER.error("Device(id={}) is not found in database", id);
            throw new EntityNotFoundException("Device(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Device save(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getAmountChannels() <= 0)
            throw new IllegalArgumentException("Amount channels <= 0");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

        entity = repository.save(entity);
        LOGGER.info("Device(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Device> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            LOGGER.info("Device(id={}) is deleted in database", optional.get().getId());
        } else {
            LOGGER.info("Device(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Device(id=" + entity.getId() + ") not found in database");
        }
    }
}
