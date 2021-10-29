package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.exception.InsertException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelNameService.class);
    private final ChannelNameRepository dao;

    private ChannelNameService(ChannelNameRepository dao) {
        this.dao = dao;
    }

    public ChannelName create(String name, String comment) throws Exception {
        if (name == null)
            throw new NullPointerException("Name is null");
        if (name.isBlank())
            throw new IllegalArgumentException("Name is blank");

        ChannelName entity = new ChannelName(name, comment);
        LOGGER.info("New channelName created");

        return entity;
    }

    public List<ChannelName> getAll() throws Exception {
        List<ChannelName> entities = dao.getAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all channelNames from database");
        } else {
            LOGGER.info("ChannelNames is not found in database");
        }

        return entities;
    }

    public ChannelName getById(Long id) throws Exception {
        if(id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        ChannelName entity = dao.getById(id);

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

        ChannelName readEntity = dao.getById(entity.getId());

        if (readEntity == null) {
            dao.insert(entity);
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

        ChannelName readEntity = dao.getById(entity.getId());

        if (readEntity != null) {
            dao.delete(entity);
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

        ChannelName readEntity = dao.getById(entity.getId());

        if (readEntity != null) {
            dao.update(entity);
            LOGGER.info("ChannelName(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("ChannelName(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("ChannelName(id=" + entity.getId() + ") not found in database");
        }
    }
}
