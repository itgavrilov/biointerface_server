package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.exception.BadRequestException;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class DeviceService {
    private final DeviceRepository repository;

    @Autowired
    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        log.info("DeviceService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("DeviceService is destruction");
    }

    public Set<Device> findAll() {
        Set<Device> entities = new TreeSet<>(repository.findAll());

        if (entities.size() > 0) {
            log.info("Get all devices from database");
        } else {
            log.info("Devices is not found in database");
        }

        return entities;
    }

    public Device getById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<Device> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get device(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            log.error("Device(id={}) is not found in database", id);
            throw new NotFoundException("Device(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Device save(Device entity) {
        if (entity == null)
            throw new BadRequestException("Entity is null");
        if (entity.getId() <= 0)
            throw new BadRequestException("Id <= 0");
        if (entity.getAmountChannels() <= 0)
            throw new BadRequestException("Amount channels <= 0");
        if (entity.getExaminations() == null)
            throw new BadRequestException("Examinations is null");

        entity = repository.save(entity);
        log.info("Device(id={}) is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(int id) {
        if (id <= 0) throw new IllegalArgumentException("Id <= 0");

        Optional<Device> optional = repository.findById(id);

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("Device(id={}) is deleted in database", id);
        } else {
            log.info("Device(id={}) not found in database", id);
            throw new NotFoundException("Device(id=" + id + ") not found in database");
        }
    }
}
