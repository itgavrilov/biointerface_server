package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.DeviceRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository dao;

    @Autowired
    private DeviceService(DeviceRepository dao) {
        this.dao = dao;
    }

    @PostConstruct
    private void init(){
        LOGGER.info("DeviceService is init");
    }

    @PreDestroy
    private void destroy(){
        LOGGER.info("DeviceService is destruction");
    }

    public List<Device> getAll() throws Exception {
        List<Device> entities = dao.getAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all devices from database");
        } else {
            LOGGER.info("Devices is not found in database");
        }

        return entities;
    }

    public Device getById(Long id) throws Exception {
        if(id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Device entity = dao.getById(id);

        if (entity != null) {
            LOGGER.info("Get device(id={}) from database", entity.getId());
        } else {
            LOGGER.error("Device(id={}) is not found in database", id);
            throw new EntityNotFoundException("Device(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void save(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getAmountChannels() <= 0)
            throw new IllegalArgumentException("Amount channels <= 0");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

        Device readEntity = dao.getById(entity.getId());

        if (readEntity == null) {
            dao.insert(entity);
            LOGGER.info("Device(id={}) is recorded in database", entity.getId());
        } else {
            LOGGER.warn("Device(id={}) already exists in database", entity.getId());
            if (readEntity.getAmountChannels() != entity.getAmountChannels()) {
                LOGGER.error("Amount of channels does not match previously recorded");
                throw new IllegalArgumentException("Amount of channels does not match previously recorded");
            }
        }
    }

    public void delete(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Device readEntity = dao.getById(entity.getId());

        if (readEntity != null) {
            dao.delete(entity);
            LOGGER.info("Device(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.info("Device(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Device(id=" + entity.getId() + ") not found in database");
        }
    }

    public void update(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getAmountChannels() <= 0)
            throw new IllegalArgumentException("Amount channels <= 0");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

        Device readEntity = dao.getById(entity.getId());

        if (readEntity != null) {
            dao.update(entity);
            LOGGER.info("Device(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("Device(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Device(id=" + entity.getId() + ") not found in database");
        }
    }
}
