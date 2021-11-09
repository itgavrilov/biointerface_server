package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.repository.ChannelRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 03/11/2021
 */
@Service
public class ChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelService.class);
    private final ChannelRepository repository;

    @Autowired
    public ChannelService(ChannelRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("ChannelService is init");
    }

    @PreDestroy
    private void destroy() {
        LOGGER.info("ChannelService is destruction");
    }

    public List<Channel> findAll() throws Exception {
        List<Channel> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all channels from database");
        } else {
            LOGGER.info("Channels is not found in database");
        }

        return entities;
    }

    public List<Channel> findAllByExamination(Examination examination) throws Exception {
        List<Channel> entities = repository.findAllByExamination(examination);

        if (entities.size() > 0) {
            LOGGER.info("Get all channels by examination from database");
        } else {
            LOGGER.info("Channels by examination is not found in database");
        }

        return entities;
    }

    public Channel findById(ChannelID id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");

        Optional<Channel> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get channel(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            LOGGER.error("Channel(id={}) is not found in database", id);
            throw new EntityNotFoundException("Channel(id=" + id + ") is not found in database");
        }
    }

    @Transactional
    public Channel save(Channel entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getExamination() == null)
            throw new NullPointerException("Examination is null");
        if (entity.getSamples() == null)
            throw new NullPointerException("Samples is null");

        entity = repository.save(entity);
        LOGGER.info("Channel(id={})  is recorded in database", entity.getId());

        return entity;
    }

    @Transactional
    public void delete(Channel entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId().getNumber() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Channel> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            LOGGER.info("Channel(id={}) is deleted in database", optional.get().getId());
        } else {
            LOGGER.info("Channel(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Channel(id=" + entity.getId() + ") not found in database");
        }
    }
}