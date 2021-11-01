package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.exception.InsertException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Component
public class ChannelNameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelNameService.class);
    private final ChannelNameRepository repository;

    @Autowired
    public ChannelNameService(ChannelNameRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("ChannelNameService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("ChannelNameService is destruction");
    }

    public List<ChannelName> getAll() throws Exception {
        List<ChannelName> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all channelNames from database");
        } else {
            LOGGER.info("ChannelNames is not found in database");
        }

        return entities;
    }

    public ChannelName getById(Long id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        ChannelName entity = repository.getById(id);

        if (entity != null) {
            LOGGER.info("Get channelName(id={}) from database", entity.getId());
        } else {
            LOGGER.error("ChannelName(id={}) is not found in database", id);
            throw new EntityNotFoundException("ChannelName(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void save(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if (entity.getName().isBlank())
            throw new IllegalArgumentException("Name is blank");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");

        ChannelName readEntity = repository.getById(entity.getId());

        if (readEntity == null) {
            repository.save(entity);
            LOGGER.info("ChannelName(id={})  is recorded in database", entity.getId());
        } else {
            LOGGER.error("ChannelName(id={}) already exists in database", entity.getId());
            throw new InsertException("ChannelName(id=" + entity.getId() + ") already exists in database");
        }
    }

    public void delete(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        ChannelName readEntity = repository.getById(entity.getId());

        if (readEntity != null) {
            repository.delete(entity);
            LOGGER.info("ChannelName(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.info("ChannelName(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("ChannelName(id=" + entity.getId() + ") not found in database");
        }
    }

    public void update(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if (entity.getName().isBlank())
            throw new IllegalArgumentException("Name is blank");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");

        ChannelName readEntity = repository.getById(entity.getId());

        if (readEntity != null) {
            repository.save(entity);
            LOGGER.info("ChannelName(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("ChannelName(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("ChannelName(id=" + entity.getId() + ") not found in database");
        }
    }
}
