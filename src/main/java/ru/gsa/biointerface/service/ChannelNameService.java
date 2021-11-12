package ru.gsa.biointerface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@Service
public class ChannelNameService {
    private final ChannelNameRepository repository;

    @Autowired
    public ChannelNameService(ChannelNameRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        log.info("ChannelNameService is init");
    }

    @PreDestroy
    private void destroy() {
        log.info("ChannelNameService is destruction");
    }

    public List<ChannelName> getAll() throws Exception {
        List<ChannelName> entities = repository.findAll();

        if (entities.size() > 0) {
            log.info("Get all channelNames from database");
        } else {
            log.info("ChannelNames is not found in database");
        }

        return entities;
    }

    public ChannelName findById(int id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<ChannelName> optional = repository.findById(id);

        if (optional.isPresent()) {
            log.info("Get channelName(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            log.error("ChannelName(id={}) is not found in database", id);
            throw new EntityNotFoundException("ChannelName(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public ChannelName save(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if (entity.getName().isBlank())
            throw new IllegalArgumentException("Name is blank");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");

        entity = repository.save(entity);
        log.info("ChannelName(id={})  is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<ChannelName> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            log.info("ChannelName(id={}) is deleted in database", optional.get().getId());
        } else {
            log.info("ChannelName(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("ChannelName(id=" + entity.getId() + ") not found in database");
        }
    }
}
